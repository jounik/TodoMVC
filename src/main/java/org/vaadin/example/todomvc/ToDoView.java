package org.vaadin.example.todomvc;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;

public class ToDoView extends CssLayout {
    private TextField newTodo;
    private CssLayout main;
    private Toggle toggleAll;
    private Label todoCount;
    private NativeButton clearCompleted;
    private NativeButton all;
    private NativeButton active;
    private NativeButton completed;

    private boolean newTodoFocused;
    private Set<TodoRow> rows = new HashSet<TodoRow>();
    private int itemsCompleted = 0;

    public ToDoView() {
        setSizeUndefined();
        setId("todoapp");

        Label header = new Label("<h1>todos</h1>", ContentMode.HTML);
        header.setId("header");
        header.setSizeUndefined();
        addComponent(header);

        newTodo = new TextField();
        newTodo.setId("new-todo");
        newTodo.setInputPrompt("What needs to be done?");
        addComponent(newTodo);

        addComponent(main = new CssLayout() {
            {
                setId("main");
                toggleAll = new Toggle("Mark all as complete");
                toggleAll.setId("toggle-all");
                addComponent(toggleAll);
            }
        });

        toggleAll.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean completed = toggleAll.getValue().booleanValue();
                for (TodoRow row : rows) {
                    row.setCompleted(completed);
                }
            }
        });

        newTodo.addFocusListener(new FocusListener() {

            @Override
            public void focus(FocusEvent event) {
                newTodoFocused = true;
            }
        });
        newTodo.addBlurListener(new BlurListener() {

            @Override
            public void blur(BlurEvent event) {
                newTodoFocused = false;
            }
        });
        addShortcutListener(new ShortcutListener(null, KeyCode.ENTER, null) {

            @Override
            public void handleAction(Object sender, Object target) {
                if (newTodoFocused) {
                    String value = newTodo.getValue();
                    if (!value.isEmpty()) {
                        main.addComponent(getNewTodoRow(value, false, false));
                        newTodo.setValue("");
                    }
                } else {
                    getUI().focus();
                }
            }
        });

        addComponent(new CssLayout() {
            {
                setId("footer");

                todoCount = new Label("<b>0</b> items left", ContentMode.HTML);
                todoCount.setId("todo-count");
                todoCount.setSizeUndefined();
                addComponent(todoCount);

                CssLayout filters = new CssLayout();
                filters.setId("filters");
                addComponent(filters);

                all = new NativeButton("All");
                active = new NativeButton("Active");
                completed = new NativeButton("Completed");

                all.addStyleName("selected");
                filters.addComponents(all, active, completed);

                clearCompleted = new NativeButton("Clear completed (0)");
                clearCompleted.setId("clear-completed");
                addComponent(clearCompleted);

                all.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        all.addStyleName("selected");
                        active.removeStyleName("selected");
                        completed.removeStyleName("selected");
                        for (TodoRow row : rows) {
                            row.setVisible(true);
                        }
                    }
                });
                active.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        all.removeStyleName("selected");
                        active.addStyleName("selected");
                        completed.removeStyleName("selected");
                        for (TodoRow row : rows) {
                            row.setVisible(!row.isCompleted());
                        }
                    }
                });
                completed.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        all.removeStyleName("selected");
                        active.removeStyleName("selected");
                        completed.addStyleName("selected");
                        for (TodoRow row : rows) {
                            row.setVisible(row.isCompleted());
                        }
                    }
                });

                clearCompleted.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Set<TodoRow> tbd = new HashSet<ToDoView.TodoRow>();
                        for (TodoRow row : rows) {
                            if (row.isCompleted()) {
                                tbd.add(row);
                            }
                        }
                        for (TodoRow row : tbd) {
                            row.delete();
                        }
                    }
                });
            }
        });

        updateCounters();
    }

    private TodoRow getNewTodoRow(final String captionText, final boolean done,
            final boolean editing) {
        TodoRow row = new TodoRow(captionText, done, editing);
        rows.add(row);
        updateCounters();
        return row;
    }

    private void updateCounters() {
        int itemsLeft = rows.size() - itemsCompleted;
        todoCount.setValue("<b>" + itemsLeft + "</b> items left");
        clearCompleted.setCaption("Clear completed (" + itemsCompleted + ")");
        clearCompleted.setVisible(itemsCompleted != 0);
        toggleAll.setInternalValue(itemsLeft == 0);
    }

    public class Toggle extends CheckBox {

        public Toggle(String caption) {
            super(caption);
        }

        @Override
        public void setInternalValue(Boolean newValue) {
            super.setInternalValue(newValue);
        }
    }

    private class TodoRow extends CssLayout {
        private CheckBox checkbox;

        public TodoRow(final String captionText, final boolean done,
                final boolean editing) {
            addStyleName("todo-row");
            if (done) {
                addStyleName("completed");
            }
            if (editing) {
                addStyleName("editing");
            }

            checkbox = new CheckBox(null, done);
            addComponent(checkbox);

            final Label caption = new Label(captionText);
            caption.setSizeUndefined();
            addComponent(caption);

            NativeButton destroy = new NativeButton();
            destroy.addStyleName("destroy");
            addComponent(destroy);

            final TextField edit = new TextField();
            edit.setValue(captionText);
            addComponent(edit);

            checkbox.addValueChangeListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    if (checkbox.getValue().booleanValue()) {
                        addStyleName("completed");
                        itemsCompleted++;
                    } else {
                        removeStyleName("completed");
                        itemsCompleted--;
                    }
                    toggleVisibility();
                    updateCounters();
                }
            });

            addLayoutClickListener(new LayoutClickListener() {

                @Override
                public void layoutClick(LayoutClickEvent event) {
                    if (event.isDoubleClick()
                            && caption == event.getClickedComponent()) {
                        addStyleName("editing");
                        edit.selectAll();
                        edit.focus();
                    }
                }
            });

            edit.addBlurListener(new BlurListener() {

                @Override
                public void blur(BlurEvent event) {
                    removeStyleName("editing");
                    caption.setValue(edit.getValue());
                }
            });

            destroy.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    delete();
                }
            });

            toggleVisibility();
        }

        private void toggleVisibility() {
            if (checkbox.getValue().booleanValue()) {
                setVisible(!active.getStyleName().contains("selected"));
            } else {
                setVisible(!completed.getStyleName().contains("selected"));
            }
        }

        public boolean isCompleted() {
            return checkbox.getValue().booleanValue();
        }

        public void setCompleted(boolean completed) {
            checkbox.setValue(completed);
        }

        public void delete() {
            main.removeComponent(TodoRow.this);
            rows.remove(TodoRow.this);
            if (checkbox.getValue().booleanValue()) {
                itemsCompleted--;
            }
            updateCounters();
        }
    }
}
