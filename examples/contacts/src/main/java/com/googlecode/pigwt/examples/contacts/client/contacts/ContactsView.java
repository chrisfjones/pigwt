package com.googlecode.pigwt.examples.contacts.client.contacts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.googlecode.pigwt.client.Pigwt;
import com.googlecode.pigwt.examples.contacts.client.Contact;

import java.util.Comparator;
import java.util.List;

public class ContactsView extends Composite {
    public interface ContactsViewUiBinder extends UiBinder<Widget, ContactsView> {
    }
    private ListDataProvider<Contact> dataProvider;
    private List<Contact> list;
    @UiField SimplePanel cellListContainer;
    CellTable<Contact> cellTable;

    public void displayContacts(List<Contact> result) {
        for (Contact contact : result) {
            list.add(contact);
        }
    }
    
    public ContactsView() {
        final ContactsViewUiBinder uiBinder = (ContactsViewUiBinder) GWT.create(ContactsViewUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));

        cellTable = new CellTable<Contact>();

        TextColumn<Contact> firstColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact contact) {
                return contact.getFirst();
            }
        };
        firstColumn.setSortable(true);
        cellTable.addColumn(firstColumn, "First Name");

        TextColumn<Contact> lastColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact contact) {
                return contact.getLast();
            }
        };
        lastColumn.setSortable(true);
        cellTable.addColumn(lastColumn, "Last Name");

        dataProvider = new ListDataProvider<Contact>();
        dataProvider.addDataDisplay(cellTable);
        list = dataProvider.getList();

        // Add a ColumnSortEvent.ListHandler to connect sorting to the
        // java.util.List.
        ColumnSortEvent.ListHandler<Contact> columnSortHandler = new ColumnSortEvent.ListHandler<Contact>(list);
        columnSortHandler.setComparator(firstColumn,
                new Comparator<Contact>() {
                    public int compare(Contact o1, Contact o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the name columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getFirst().compareTo(o2.getFirst()) : 1;
                        }
                        return -1;
                    }
                });
        cellTable.addColumnSortHandler(columnSortHandler);

        // We know that the data is sorted alphabetically by default.
        cellTable.getColumnSortList().push(firstColumn);

        cellTable.addHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                Pigwt.get().goTo("contact.demographic", "chris", "jones" + i++);
            }
        }, ClickEvent.getType());

        cellListContainer.setWidget(cellTable);
    }

    private static int i = 0;
}
