package com.ssp.platform.controller;

import com.ssp.platform.exceptions.*;
import com.ssp.platform.logging.*;
import com.ssp.platform.request.SupplyUpdateRequest;
import com.ssp.platform.response.*;
import com.ssp.platform.entity.*;
import com.ssp.platform.entity.enums.SupplyStatus;
import com.ssp.platform.security.service.UserDetailsServiceImpl;
import com.ssp.platform.service.SupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Контроллер для действий предложениями с закупками
 * @author Горбунов Александр
 */
@RestController
public class SupplyController {

    private final SupplyService supplyService;
    private final UserDetailsServiceImpl userDetailsService;

    private final Log log;

    @Autowired
    public SupplyController(SupplyService supplyService, UserDetailsServiceImpl userDetailsService, Log log) {
        this.supplyService = supplyService;
        this.userDetailsService = userDetailsService;
        this.log = log;
    }

    @PostMapping("/supply")
    @PreAuthorize("hasAuthority('firm')")
    public ResponseEntity<Object> createSupply(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID purchaseId,
            @RequestParam String description,
            @RequestParam(required = false) Long budget,
            @RequestParam(required = false) String comment,
            @RequestParam(value = "files", required = false) MultipartFile[] files)
            throws IOException, NoSuchAlgorithmException, SupplyValidationException, FileValidationException, SupplyServiceException {

        User author = userDetailsService.loadUserByToken(token);
        if (author.getStatus().equals("NotApproved")) return new ResponseEntity<>(new ApiResponse(false, "Пользователь не аккредитован!"), HttpStatus.BAD_REQUEST);

        supplyService.create(purchaseId, description, author, budget, comment, files);

        log.info(author, Log.CONTROLLER_SUPPLY, "Предложение создано", purchaseId, description, budget, comment);

        return new ResponseEntity<>(new ApiResponse(true, "Предложение создано"), HttpStatus.OK);
    }

    @PutMapping(value = "/supply/{id}")
    @PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
    public ResponseEntity<Object> updateSupply(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long budget,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) SupplyStatus status,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) MultipartFile[] files)
            throws IOException, NoSuchAlgorithmException, SupplyValidationException, FileValidationException, SupplyServiceException {

        SupplyUpdateRequest updateRequest = new SupplyUpdateRequest(description, budget, comment, status, result, files);

        User user = userDetailsService.loadUserByToken(token);

        SupplyEntity wasEntity = supplyService.get(user, id);
        Object[] was = {wasEntity.getDescription(), wasEntity.getBudget(), wasEntity.getComment(), wasEntity.getStatus(), wasEntity.getResult()};

        supplyService.update(user, id, updateRequest);

        SupplyEntity becameEntity = supplyService.get(user, id);
        Object[] became = {becameEntity.getDescription(), becameEntity.getBudget(), becameEntity.getComment(), becameEntity.getStatus(), becameEntity.getResult()};

        log.info(user, Log.CONTROLLER_SUPPLY, "Предложение обновлено", was, became);

        return new ResponseEntity<>(supplyService.get(user, id), HttpStatus.OK);
    }

    @DeleteMapping("/supply/{id}")
    @PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
    public ResponseEntity<Object> deleteSupply(@RequestHeader("Authorization") String token, @PathVariable("id") UUID id)
            throws IOException, FileServiceException, SupplyServiceException {
        User user = userDetailsService.loadUserByToken(token);
        supplyService.delete(user, id);

        log.info(user, Log.CONTROLLER_SUPPLY, "Предложение удалено", id);

        return new ResponseEntity<>(new ApiResponse(true, "Предложение удалено"), HttpStatus.OK);
    }

    @GetMapping("/supply/{id}")
    @PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
    public ResponseEntity<Object> getSupply(@RequestHeader("Authorization") String token, @PathVariable("id") UUID id) throws SupplyServiceException
    {
        User user = userDetailsService.loadUserByToken(token);

        return new ResponseEntity<>(supplyService.get(user, id), HttpStatus.OK);
    }

    @GetMapping("/supplies")
    @PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
    public ResponseEntity<Object> getPageOfSupplies(@RequestParam UUID purchaseId) throws SupplyServiceException
    {
        List<SupplyEntity> list = supplyService.getList(purchaseId);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
