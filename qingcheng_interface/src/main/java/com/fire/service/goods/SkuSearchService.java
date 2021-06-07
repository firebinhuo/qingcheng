package com.fire.service.goods;

import java.util.Map;

public interface SkuSearchService {

    abstract Map search(Map<String, String> searchMap);
}
