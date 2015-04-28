package org.vaadin.example.todomvc;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("todomvc")
@PreserveOnRefresh
public class TodoMvcUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TodoMvcUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeUndefined();

        CssLayout root = new CssLayout();
        setContent(root);

        root.addComponent(new ToDoView());

        Label info = new Label(
                "<p>Double-click to edit a todo</p>"
                        + "<p>Written by <a href=\"https://github.com/jounik\">Jouni Koivuviita</a> and <a href=\"https://github.com/marlonrichert\">Marlon Richert</a></p>"
                        + "<p>Part of <a href=\"http://todomvc.com\">TodoMVC</a></p>",
                ContentMode.HTML);
        info.setId("info");
        root.addComponent(info);

    }

}
