package com.fire.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.goods.Album;
import com.fire.service.goods.AlbumService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/album")
public class AlbumController {


    public int fn(int a, int b) {
        return a > b ? 1 : 0;
    }


    @Reference
    private AlbumService albumService;

    @GetMapping("/findAll")
    public List<Album> findAll() {
        return albumService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Album> findPage(int page, int size) {
        return albumService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Album> findList(@RequestBody Map<String, Object> searchMap) {
        return albumService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Album> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return albumService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Album findById(Long id) {
        return albumService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Album album) {
        albumService.add(album);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Album album) {
        albumService.update(album);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id) {
        albumService.delete(id);
        return new Result();
    }

}
