package org.vaadin.example;

import com.flowingcode.addons.ycalendar.MonthCalendar;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Random;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
//@PWA(name = "Vaadin Application",
//        shortName = "Vaadin App",
//        description = "This is an example Vaadin application.")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/test_year-month-calendar.css", themeFor = "vaadin-month-calendar")
public class MainView extends VerticalLayout {

    private YearMonth currentStartMonth = YearMonth.now();
    private int calendarCounter = 3;
    private VerticalLayout calendar = null;

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
    public MainView(@Autowired GreetService service) {
        Button buttonCT = new Button("Change Theme");
        buttonCT.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList(); // (1)
            if (themeList.contains(Lumo.DARK)) { // (2)
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
        add(buttonCT);

        calendar = createCalendar();
        add(calendar);
        updateCalendar();
    }

    private VerticalLayout createCalendar() {

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("200px", 2),
                new FormLayout.ResponsiveStep("600px", 6));

        HorizontalLayout h = new HorizontalLayout();
        h.setWidthFull();
        h.setJustifyContentMode(JustifyContentMode.BETWEEN);
        h.setSpacing(false);
        h.setPadding(true);
        Button left = new Button("<--", VaadinIcon.ARROW_CIRCLE_LEFT_O.create(), a -> {
            currentStartMonth = currentStartMonth.plusMonths(-1);
            updateCalendar();
        });
        left.addThemeVariants(ButtonVariant.LUMO_SMALL);
        h.add(left);
        Button right = new Button("-->", VaadinIcon.ARROW_CIRCLE_RIGHT_O.create(), a -> {
            currentStartMonth = currentStartMonth.plusMonths(1);
            updateCalendar();
        });
        right.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button add = new Button("Month", VaadinIcon.PLUS_CIRCLE_O.create(), a -> {
            calendarCounter++;
            if (calendarCounter > 6) {
                calendarCounter = 6;
            }
            updateCalendar();
        });
        add.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button substract = new Button("Month", VaadinIcon.MINUS_CIRCLE_O.create(), a -> {
            calendarCounter--;
            if (calendarCounter == 0) {
                calendarCounter = 1;
            }
            updateCalendar();
        });
        substract.addThemeVariants(ButtonVariant.LUMO_SMALL);


        HorizontalLayout middle = new HorizontalLayout(add, substract);

//        FlexLayout middleWrapper = new FlexLayout(middle);
//        middleWrapper.setJustifyContentMode(JustifyContentMode.CENTER);
//        right.getElement().getStyle().set("margin-left", "auto");
        h.add(middle);

//        FlexLayout rightButtonWrapper = new FlexLayout(right);
//        rightButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        right.getElement().getStyle().set("margin-left", "auto");
        h.add(right);

        VerticalLayout top = new VerticalLayout(h, layout);
        top.setSpacing(false);
        top.setPadding(false);
        top.setWidthFull();
        top.setHeightFull();
        return top;
    }

    private void updateCalendar() {
        if (calendar == null) {
            return;
        }
        FormLayout f = (FormLayout) calendar.getComponentAt(1);
        f.removeAll();
        Random r = new Random();
        for (int i = 0; i < calendarCounter; i++) {
            MonthCalendar cal = new MonthCalendar(currentStartMonth.plusMonths(i));
            cal.setClassNameGenerator(date -> {
//                if (dateStatusMap.containsKey(date)) {
//                    return dateStatusMap.get(date) != null ? dateStatusMap.get(date).name() : "UNKNOWN";
//                }
                if (r.nextInt(10) > 8) {
                    return "randomBackground";
                }
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    return "weekend";
                }
                return null;
            });
            f.add(cal);
        }
        f.setResponsiveSteps(
                new FormLayout.ResponsiveStep("200px", calendarCounter > 2 ? 2 : 1),
                new FormLayout.ResponsiveStep("600px", calendarCounter));
    }

}
