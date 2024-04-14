package br.ufscar.ppgcc.common.views;

import br.ufscar.ppgcc.domain.carrier.CarrierListView;
import br.ufscar.ppgcc.domain.device.DeviceListView;
import br.ufscar.ppgcc.domain.sensor.SensorTypeListView;
import br.ufscar.ppgcc.domain.product.ProductListView;
import br.ufscar.ppgcc.domain.freight.FreightListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Cold Chain Tracking");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Devices", DeviceListView.class, LineAwesomeIcon.MICROCHIP_SOLID.create()));
        nav.addItem(new SideNavItem("Sensor Types", SensorTypeListView.class, LineAwesomeIcon.TEMPERATURE_HIGH_SOLID.create()));
        nav.addItem(new SideNavItem("Products", ProductListView.class, LineAwesomeIcon.BOX_SOLID.create()));
        nav.addItem(new SideNavItem("Carriers", CarrierListView.class, LineAwesomeIcon.HARD_HAT_SOLID.create()));
        nav.addItem(new SideNavItem("Freights", FreightListView.class, LineAwesomeIcon.ROUTE_SOLID.create()));
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
