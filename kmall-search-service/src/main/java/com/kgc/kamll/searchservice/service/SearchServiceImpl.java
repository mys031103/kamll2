package com.kgc.kamll.searchservice.service;

import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchSortValues;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchSkuParam pmsSearchSkuParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuParam.getSkuAttrValueList();

        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        if (catalog3Id!=null){
            TermQueryBuilder termQueryBuilder=new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (skuAttrValueList!=null){
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId",catalog3Id);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        if (keyword!=null&&keyword.isEmpty()==false){

            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("id", SortOrder.DESC);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        //高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        String dsl = searchSourceBuilder.toString();
        System.out.println(dsl);
        Search search=new Search.Builder(dsl).addIndex("kmall").addType("PmsSkuInfo").build();
        try {
            SearchResult searchResult = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
                //获取高亮数据
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight!=null){
                    String skuName = highlight.get("skuName").get(0);
                    //使用高亮的skuName替换原来的skuName
                    pmsSearchSkuInfo.setSkuName(skuName);
                }
                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pmsSearchSkuInfos;
    }


}
