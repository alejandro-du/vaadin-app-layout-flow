package com.vaadin.flow.component.applayout;

/*
 * #%L
 * Vaadin App Layout for Vaadin 10
 * %%
 * Copyright (C) 2017 - 2018 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Element;

import java.util.Objects;
import java.util.Optional;

/**
 * Menu to be used with AppLayout. Provides clicable tabs that can be used for routing or individual actions.
 */
public class AppLayoutMenu implements HasElement, AttachNotifier {

    private final Tabs tabs = new Tabs();
    private final SelectionChangeListener selectionChangeListener = new SelectionChangeListener();
    private AppLayoutMenuItem selectedMenuItem;

    /**
     * Default constructor.
     */
    public AppLayoutMenu() {
        tabs.getElement().setAttribute("theme", "minimal");
        tabs.addSelectedChangeListener(selectionChangeListener);
    }

    /**
     * Clears existing menu items and sets the new the arguments.
     *
     * @param menuItems items of the type {@link AppLayoutMenuItem} to set
     */
    public void setMenuItems(AppLayoutMenuItem... menuItems) {
        clearMenuItems();
        addMenuItems(menuItems);
    }

    /**
     * Adds menu items to the menu.
     *
     * @param menuItems items of the type {@link AppLayoutMenuItem} to add
     */
    public void addMenuItems(AppLayoutMenuItem... menuItems) {
        try {
            selectionChangeListener.enabled = false;
            tabs.add(menuItems);
        } finally {
            selectionChangeListener.enabled = true;
        }
        if (selectedMenuItem == null) {
            tabs.setSelectedIndex(-1);
        }
    }

    /**
     * Constructs a new object with the given title.
     *
     * @param title the title to display
     */
    public AppLayoutMenuItem addMenuItem(String title) {
        return addAndReturn(new AppLayoutMenuItem(title));
    }

    /**
     * Constructs a new object with the given icon.
     *
     * @param icon the icon to display
     */
    public AppLayoutMenuItem addMenuItem(Component icon) {
        return addAndReturn(new AppLayoutMenuItem(icon));
    }

    /**
     * Constructs a new object with the given icon and title.
     *
     * @param icon  the icon to display
     * @param title the title to display
     */
    public AppLayoutMenuItem addMenuItem(Component icon, String title) {
        return addAndReturn(new AppLayoutMenuItem(icon, title));
    }

    /**
     * Constructs a new object with the given icon, title and route.
     *
     * @param icon  the icon to display
     * @param title the title to display
     * @param route the route to navigate on click
     */
    public AppLayoutMenuItem addMenuItem(Component icon, String title,
        String route) {
        return addAndReturn(new AppLayoutMenuItem(icon, title, route));
    }

    /**
     * Constructs a new object with the given icon and click listener.
     *
     * @param icon     the icon to display
     * @param listener the menu item click listener
     */
    public AppLayoutMenuItem addMenuItem(Component icon,
        ComponentEventListener<MenuItemClickEvent> listener) {
        return addAndReturn(new AppLayoutMenuItem(icon, listener));
    }

    /**
     * Constructs a new object with the given title and click listener.
     *
     * @param title    the title to display
     * @param listener the menu item click listener
     */
    public AppLayoutMenuItem addMenuItem(String title,
        ComponentEventListener<MenuItemClickEvent> listener) {
        return addAndReturn(new AppLayoutMenuItem(title, listener));
    }

    /**
     * Constructs a new object with the given icon, title and click listener.
     *
     * @param icon     the icon to display
     * @param title    the title to display
     * @param listener the menu item click listener
     */
    public AppLayoutMenuItem addMenuItem(Component icon, String title,
        ComponentEventListener<MenuItemClickEvent> listener) {
        return addAndReturn(new AppLayoutMenuItem(icon, title, listener));
    }

    private AppLayoutMenuItem addAndReturn(AppLayoutMenuItem item) {
        addMenuItems(item);
        return item;
    }

    /**
     * Removes {@link AppLayoutMenuItem} from the menu
     */
    public void removeMenuItem(AppLayoutMenuItem menuItem) {
        if (Objects.equals(this.selectedMenuItem, menuItem)) {
            this.selectedMenuItem = null;
        }
        tabs.remove(menuItem);
    }

    /**
     * Removes all menu items.
     */
    public void clearMenuItems() {
        selectedMenuItem = null;
        tabs.removeAll();
    }

    /**
     * Selects a menu item.
     *
     * @param menuItem {@link AppLayoutMenuItem} to select
     */
    public void selectMenuItem(AppLayoutMenuItem menuItem) {
        selectedMenuItem = menuItem;
        tabs.setSelectedTab(menuItem);
    }

    /**
     * Gets the first {@link AppLayoutMenuItem} targeting a route.
     *
     * @param route route to match to {@link AppLayoutMenuItem#getRoute()}
     * @return {@link AppLayoutMenuItem} wrapped in an {@link Optional}, if found.
     */
    public Optional<AppLayoutMenuItem> getMenuItemTargetingRoute(String route) {
        Objects.requireNonNull(route, "Route can not be null");
        return tabs.getChildren().map(AppLayoutMenuItem.class::cast)
            .filter(e -> route.equals(e.getRoute())).findFirst();
    }

    /**
     * Gets the currently selected menu item.
     *
     * @return {@link AppLayoutMenuItem} selected menu item.
     */
    public AppLayoutMenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getElement() {
        return tabs.getElement();
    }

    private class SelectionChangeListener
        implements ComponentEventListener<Tabs.SelectedChangeEvent> {

        private boolean enabled = true;

        @Override
        public void onComponentEvent(Tabs.SelectedChangeEvent event) {

            final AppLayoutMenuItem selectedTab = (AppLayoutMenuItem) tabs
                .getSelectedTab();

            if (enabled && selectedTab != null) {
                if (selectedTab.getRoute() == null) {
                    // If there is no route associated, set previous tab as selected
                    if (selectedMenuItem != null) {
                        tabs.setSelectedTab(selectedMenuItem);
                    } else {
                        tabs.setSelectedIndex(-1);
                    }
                } else {
                    // Update selected tab if it is associated with a route.
                    selectedMenuItem = selectedTab;
                }
                selectedTab.fireMenuItemClickEvent();
            }
        }
    }
}