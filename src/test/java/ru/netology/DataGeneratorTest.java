package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

public class DataGeneratorTest {
    @Test
    void shouldCheckDeliveryDate() {
        Configuration.holdBrowserOpen = true;   // чтобы браузер не закрывался
        open("http://localhost:9999"); // открытие страницы
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser("Ru");

        //Заполнение и первоначальная отправка формы:
        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String scheduledDate = DataGenerator.generateDate(5);
        $("[data-test-id='date'] input").setValue(scheduledDate);
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + scheduledDate), Duration.ofSeconds(15));

        //Изменение ранее введенной даты и отправка формы:
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String shiftDate = DataGenerator.generateDate(12);
        $("[data-test-id='date'] input").setValue(shiftDate);
        $(byText("Запланировать")).click();

        //Взаимодействие с опцией перепланировки,
        //содержание текста и время загрузки:
        $("[data-test-id= replan-notification]")
                .shouldHave(Condition.text("Необходимо подтверждение " +
                        "У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofSeconds(15));
        $("[data-test-id=replan-notification] .button")
                .shouldHave(Condition.text("Перепланировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + shiftDate), Duration.ofSeconds(15));
    }
}
