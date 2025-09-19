package com.example.catalog.controller;


import com.example.catalog.service.ShowPriceRuleService;
import com.example.catalog.transfer.show.price.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalogue/price-rule")
public class ShowPriceRuleController {

    @Autowired
    private ShowPriceRuleService showPriceRuleService;

    @PostMapping
    public ShowPriceRuleCreateResponse createShowPriceRule(@RequestBody ShowPriceRuleCreateRequest request) {
        return showPriceRuleService.createShowPriceRule(request);
    }

    @GetMapping("/{ruleSystemCode}")
    public ShowPriceRuleResponse getShowPriceRuleById(@PathVariable String ruleSystemCode) {
        return showPriceRuleService.getShowPriceRule(ruleSystemCode);
    }

    @PutMapping
    public ShowPriceRuleUpdateResponse updateShowPriceRule(@RequestBody ShowPriceRuleUpdateRequest request) {
        return showPriceRuleService.updateShowPriceRule(request);
    }

    @DeleteMapping("/{ruleSystemCode}")
    public ShowPriceRuleDeleteResponse deleteShowPriceRule(@PathVariable String ruleSystemCode) {
        return showPriceRuleService.deleteShowPriceRule(ruleSystemCode);
    }
}