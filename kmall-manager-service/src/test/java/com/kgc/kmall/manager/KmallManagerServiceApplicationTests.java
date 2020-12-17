package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {
@Resource
	CatalogService catalogService;
	@Test
	void contextLoads() {
		List<PmsBaseCatalog1> catalog1 = catalogService.getCatalog1();
		for (PmsBaseCatalog1 pmsBaseCatalog1 : catalog1) {
			System.out.println(pmsBaseCatalog1.toString());
		}
	}

}
