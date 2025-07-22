package com.project.demo.code.controller.demo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.domain.DemoChickenSoup;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.service.DemoChickenSoupService;
import com.project.demo.common.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 鸡汤表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年04月07日 17:33:29
 */
@RestController
@RequestMapping("/chickenSoup")
public class DemoChickenSoupController {
        @Resource
        private DemoChickenSoupService demochickensoupService;

        /**
         * 根据ID查询
         */
        @GetMapping("/{id}")
        public DemoChickenSoup getById(@PathVariable Long id) {
                return demochickensoupService.getById(id);
        }

        /**
         * 查询所有字段（支持动态条件）
         */
        @PostMapping("queryAll")
        public Result<IPage<DemoChickenSoup>> queryAll(@RequestBody DemoChickenSoup dto) {
                return demochickensoupService.queryAll(dto);
        }

        /**
         * 根据ID更新数据
         */
        @PostMapping("update")
        public Integer update(@RequestBody DemoChickenSoup dto) {
                return demochickensoupService.update(dto);
        }

        /**
         * 新增数据
         */
        @PostMapping("add")
        public Integer add(@RequestBody DemoChickenSoup dto) {
                return demochickensoupService.insert(dto);
        }

        /**
         * 根据ID删除数据（物理删除）
         */
        @DeleteMapping("/{id}")
        public Integer deleteById(@PathVariable Integer id) {
                return demochickensoupService.deleteById(id);
        }
}
