package cn.shuniverse.base.controller;

import cn.shuniverse.base.core.resp.R;
import cn.shuniverse.base.service.TikaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 蛮小满Sama at 2025-09-26 17:02
 *
 * @author 蛮小满Sama
 * @description
 */
@RestController
@RequestMapping("/api/tika")
@RequiredArgsConstructor
public class TikaController {

    private final TikaService tikaService;

    @PostMapping("/parse")
    public R<TikaService.TikaResult> parseFile(@RequestParam("file") MultipartFile file) throws Exception {
        return R.success(tikaService.parseAll(file));
    }
}

