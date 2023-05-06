package com.ssp.platform.telegram;

import com.ssp.platform.entity.*;
import com.ssp.platform.service.UserService;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@EnableAsync
public class TelegramPurchaseNotify {
    private final String MASK_PURCHASE_NOTIFY =
            "Здравствуйте, %s!\n" +
                    "Появилась новая закупка:\n\n" +
                    "*%s*\n" +
                    "_%s_\n\n" +
                    "Бюджет: %s\n" +
                    "Дата окончания работ по закупке: %s\n" +
                    "Срок подачи предложений: %s";

    private final String MASK_DATE_SUPPLIES_DEADLINE = "yyyy MMMM E HH:mm";
    private final String MASK_DATE_PURCHASE_DEADLINE = "yyyy MMMM E";

    private final long THREE_THOUSAND_YEARS = 32503741200L;
    public static final long DATE_DIVIDER = 1000L;

    private final UserService userService;

    public TelegramPurchaseNotify(UserService userService) {
        this.userService = userService;
    }

    @Async
    public void notifyOne(TelegramLongPollingBot bot, SendMessage message, Purchase purchase, TelegramUsersEntity user){
        SimpleDateFormat dateSuppliesDeadLine = new SimpleDateFormat(MASK_DATE_SUPPLIES_DEADLINE);
        SimpleDateFormat datePurchaseDeadLine = new SimpleDateFormat(MASK_DATE_PURCHASE_DEADLINE);

        message.setText(String.format(
                MASK_PURCHASE_NOTIFY,
                userService.findByUsername(user.getUsername()).get().getFirstName(),
                purchase.getName(),
                purchase.getDescription(),
                purchase.getBudget() == 0L ? "не указан" : purchase.getBudget(),
                purchase.getFinishDeadLine() == THREE_THOUSAND_YEARS ? "не указана" : datePurchaseDeadLine.format(new Date(purchase.getFinishDeadLine() * DATE_DIVIDER)),
                dateSuppliesDeadLine.format(new Date(purchase.getProposalDeadLine() * DATE_DIVIDER)))
        );

        message.setChatId(String.valueOf(user.getChatId()));

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
