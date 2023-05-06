package com.ssp.platform.service.impl;

import com.ssp.platform.email.EmailSender;
import com.ssp.platform.entity.FileEntity;
import com.ssp.platform.entity.Purchase;
import com.ssp.platform.entity.SupplyEntity;
import com.ssp.platform.entity.User;
import com.ssp.platform.entity.enums.PurchaseStatus;
import com.ssp.platform.exceptions.FileServiceException;
import com.ssp.platform.exceptions.SupplyServiceException;
import com.ssp.platform.repository.PurchaseRepository;
import com.ssp.platform.request.PurchasesPageRequest;
import com.ssp.platform.service.FileService;
import com.ssp.platform.service.PurchaseService;
import com.ssp.platform.service.SupplyService;
import com.ssp.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с закупкой
 * @author Изначальный автор Рыжков Дмитрий, доработал Василий Воробьев
 */
@Service
@EnableScheduling
public class PurchaseServiceImpl implements PurchaseService
{
    private final FileService fileService;
    private final PurchaseRepository purchaseRepository;
    private final SupplyService supplyService;
    private final UserService userService;
    private final EmailSender emailSender;

    @Autowired
    PurchaseServiceImpl(FileService fileService, PurchaseRepository purchaseRepository, SupplyService supplyService,
                        UserService userService, EmailSender emailSender)
    {
        this.fileService = fileService;
        this.purchaseRepository = purchaseRepository;
        this.supplyService = supplyService;
        this.userService = userService;
        this.emailSender = emailSender;
    }

    /**
     * Сохранение закупки
     * @param purchase закупка
     */
    @Override
    public Purchase save(Purchase purchase)
    {
        return purchaseRepository.saveAndFlush(purchase);
    }

    /**
     * Получение одной закупки
     * @param id id закупки
     */
    @Override
    public Purchase get(UUID id)
    {
        return purchaseRepository.getOne(id);
    }

    /**
     * Получение списка закупок
     * @param purchasesPageRequest сущность с данными по поиску и пагинации
     */
    @Override
    public Page<Purchase> getAll(PurchasesPageRequest purchasesPageRequest)
    {
        Pageable pageable = PageRequest.of(purchasesPageRequest.getRequestPage(), purchasesPageRequest.getNumberOfElements(),
                Sort.by("createDate").descending());

        //в будущем через criteria когда будет больше параметров фильтрации
        String name = purchasesPageRequest.getFilterName();
        PurchaseStatus status = purchasesPageRequest.getFilterStatus();
        if (status == null)
        {
            if(name.equals("")) return purchaseRepository.findAll(pageable);
            else return purchaseRepository.findAllByNameContainingIgnoreCase(name, pageable);
        }
        else
        {
            if(name.equals("")) return purchaseRepository.findAllByStatus(status, pageable);
            else return purchaseRepository.findAllByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        }

    }

    /**
     * Удаление закупки
     * @param purchase закупка
     */
    @Override
    public boolean deletePurchase(Purchase purchase) throws IOException, FileServiceException, SupplyServiceException
    {
        List<SupplyEntity> supplies = purchase.getSupplies();
        for (SupplyEntity supply : supplies)
        {
            supplyService.delete(purchase.getAuthor(), supply.getId());
        }

        List<FileEntity> files = purchase.getFiles();
        for (FileEntity file : files)
        {
            fileService.delete(file.getId());
        }

        purchaseRepository.delete(purchase);
        return true;
    }

    /**
     * Получение закупки по id
     * @param id id закупки
     */
    @Override
    public Optional<Purchase> findById(UUID id)
    {
        return purchaseRepository.findById(id);
    }

    /**
     * Автоматическое обновление статусов закупок, один раз в минуту
     */
    @Scheduled(fixedDelay = 60000)
    public void updateStatus()
    {
        List<Purchase> purchases = purchaseRepository.findByStatusOrStatus(PurchaseStatus.bidAccepting, PurchaseStatus.bidReview);
        long nowSec = System.currentTimeMillis() / 1000;
        for (Purchase purchase : purchases)
        {
            PurchaseStatus oldStatus = purchase.getStatus();

            if (nowSec >= purchase.getFinishDeadLine())
            {
                purchase.setStatus(PurchaseStatus.finished);
                purchaseRepository.save(purchase);
            }
            else if (oldStatus == PurchaseStatus.bidAccepting && nowSec >= purchase.getProposalDeadLine())
            {
                purchase.setStatus(PurchaseStatus.bidReview);
                purchaseRepository.save(purchase);
            }
        }
    }

    /**
     * Оповещение при создании закупки
     * @param purchase закупка
     */
    @Override
    public void sendEmail(Purchase purchase)
    {
        List<User> users = userService.findByRoleAndStatus("firm", "Approved");

        emailSender.sendMailPurchaseCreate(purchase, users);
    }
}
