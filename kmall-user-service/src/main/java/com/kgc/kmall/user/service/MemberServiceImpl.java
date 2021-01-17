package com.kgc.kmall.user.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.Member;
import com.kgc.kmall.bean.MemberExample;
import com.kgc.kmall.bean.Member_Receive_Address;
import com.kgc.kmall.bean.Member_Receive_AddressExample;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.user.mapper.MemberMapper;
import com.kgc.kmall.user.mapper.Member_Receive_AddressMapper;
import com.kgc.kmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;


import javax.annotation.Resource;
import java.util.List;

@Component
@Service
public class MemberServiceImpl implements MemberService {
    @Resource
    MemberMapper memberMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    Member_Receive_AddressMapper member_receive_addressMapper;

    @Override
    public List<Member> selectAllMember() {
        return memberMapper.selectByExample(null);
    }

    @Override
    public Member login(Member member) {
        //先从redis中进行查询
        Jedis jedis = null;
        try {

            jedis = redisUtil.getJedis();
            if (jedis != null) {
                String umsMemberStr = jedis.get("user:" + member.getUsername() + ":info");
                if (StringUtils.isNotBlank(umsMemberStr)) {
                    //密码正确
                    Member umsMemberFromCache = JSON.parseObject(umsMemberStr, Member.class);
                    System.out.println("缓存");
                    return umsMemberFromCache;
                }
            }
            //连接redis失败，开启数据库查询
            Member umsMemberFromDb = loginFromDb(member);
            if (umsMemberFromDb != null) {
                jedis.setex("user:" + umsMemberFromDb.getId() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
            }
            System.out.println("数据库");
            return umsMemberFromDb;
        } finally {
            jedis.close();
        }

    }

    private Member loginFromDb(Member member) {
        MemberExample example = new MemberExample();
        MemberExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(member.getUsername());
        criteria.andPasswordEqualTo(member.getPassword());
        List<Member> members = memberMapper.selectByExample(example);
        if (members.size() > 0)
            return members.get(0);
        return null;
    }

    @Override
    public void addUserToken(String token, Long memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:"+memberId+":token",60*60*2,token);
        jedis.close();
    }
    @Override
    public Member checkOauthUser(Long sourceUid) {
        Member member = null;
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andSourceUidEqualTo(sourceUid);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (members != null && members.size() > 0) {
            member = members.get(0);
        }
        return member;
    }

    @Override
    public void addOauthUser(Member umsMember) {
        memberMapper.insertSelective(umsMember);
    }

    @Override
    public List<Member_Receive_Address> getReceiveAddressByMemberId(Long memberId) {
        Member_Receive_AddressExample example=new Member_Receive_AddressExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        List<Member_Receive_Address> member_receive_addresses = member_receive_addressMapper.selectByExample(example);
        return member_receive_addresses;
    }
}
