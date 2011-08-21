package com.googlecode.pigwt.examples.bigapp.client.attributes;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AttributesView extends CellTable<Attribute> implements AttributesActivity.View {
    public static interface AttributesViewUiBinder extends UiBinder<CellTable<Attribute>, AttributesView>{}

    private ListDataProvider<Attribute> dataProvider;
    
    @Inject
    public AttributesView(
            final AttributesViewUiBinder uiBinder) {
        uiBinder.createAndBindUi(this);

        TextColumn<Attribute> nameColumn = new TextColumn<Attribute>() {
            @Override
            public String getValue(Attribute attribute) {
                return attribute.getName();
            }
        };
        addColumn(nameColumn, "Attribute Name");

        dataProvider = new ListDataProvider<Attribute>();
        dataProvider.addDataDisplay(this);
    }

    @Override
    public void displayAttributes(final List<Attribute> result, int maxRows) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(result.subList(0, Math.min(maxRows, result.size())));
    }
}
