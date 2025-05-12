package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.PipelineStage;
import com.devcuong.smart_hr.dto.PipelineStageDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.PipelineStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/pipeline-stages")
public class PipelineStageController {
    @Autowired
    private PipelineStageService pipelineStageService;

    @PostMapping("search")
    public PageResponse searchPipelineStage(@RequestBody PageFilterInput<PipelineStage> input) {
        Page<Map<String, Object>> page = pipelineStageService.searchPipeline(input);
        return PageResponse.builder().dataCount(page.getTotalElements()).data(page.getContent()).build().success();
    }

    @PostMapping("create")
    public ApiResponse createPipelineStage(@RequestBody PipelineStageDTO pipelineStage) {
        return ApiResponse.builder().data(pipelineStageService.createPipeline(pipelineStage)).build().success();
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse deletePipelineStage(@PathVariable Integer id) {
        pipelineStageService.deletePipeline(id);
        return ApiResponse.builder().build().success();
    }

    @PatchMapping("update-stage-order")
    public ApiResponse updatePipelineStageOrder(@RequestBody List<PipelineStageDTO> pipelineStages) {
        pipelineStageService.updateBatchStageOrder(pipelineStages);
        return ApiResponse.builder().build().success();
    }

    @PatchMapping("update-stage-name")
    public ApiResponse updateStageName(@RequestBody PipelineStageDTO pipelineStage) {
        pipelineStageService.updateStageName(pipelineStage);
        return ApiResponse.builder().build().success();
    }
}
