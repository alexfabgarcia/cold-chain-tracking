package br.ufscar.ppgcc.common.views;

import br.ufscar.ppgcc.domain.carrier.CarrierListView;
import br.ufscar.ppgcc.domain.device.DeviceListView;
import br.ufscar.ppgcc.domain.measurement.MeasurementTypeListView;
import br.ufscar.ppgcc.domain.product.ProductListView;
import br.ufscar.ppgcc.domain.freight.FreightListView;
import br.ufscar.ppgcc.domain.security.SecurityService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.Map;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    private H2 viewTitle;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        var username = securityService.getAuthenticatedUser().getUsername();
        var logoutButton = new Button("Log out " + username, e -> securityService.logout());

        var header = new HorizontalLayout(toggle, viewTitle, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(viewTitle);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(true, header);
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
        sideNavItems().forEach((clazz, item) -> {
            if (securityService.hasAccessTo(clazz)) {
                nav.addItem(item);
            }
        });
        return nav;
    }

    private Map<Class<?>, SideNavItem> sideNavItems() {
        return Map.of(
                FreightListView.class, new SideNavItem("Freights", FreightListView.class, LineAwesomeIcon.ROUTE_SOLID.create()),
                MeasurementTypeListView.class, new SideNavItem("Measurement Types", MeasurementTypeListView.class, LineAwesomeIcon.TEMPERATURE_HIGH_SOLID.create()),
                DeviceListView.class, new SideNavItem("Devices", DeviceListView.class, LineAwesomeIcon.MICROCHIP_SOLID.create()),
                ProductListView.class, new SideNavItem("Products", ProductListView.class, LineAwesomeIcon.BOX_SOLID.create()),
                CarrierListView.class, new SideNavItem("Carriers", CarrierListView.class, LineAwesomeIcon.HARD_HAT_SOLID.create())
        );
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
