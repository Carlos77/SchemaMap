/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.info.Info;
import java.util.ArrayList;
import java.util.List;
import org.nyu.edu.dlts.client.MainEntryPoint;
import org.nyu.edu.dlts.client.model.SchemaData;
import org.nyu.edu.dlts.client.model.SchemaDataField;
import org.nyu.edu.dlts.client.model.SchemaDataFieldProperties;
import org.nyu.edu.dlts.client.model.SchemaDataProperties;

/**
 *
 * @author nathan
 */
public class SchemaDataInfoPanel implements IsWidget {

    private SchemaDataFieldProperties props = GWT.create(SchemaDataFieldProperties.class);
    private SchemaData schemaData;
    private ArrayList<SchemaData> aspaceSchemaDataList;
    
    private Grid<SchemaDataField> grid;
    
    private TextField mapToTextField;
    
    private TextField noteTextField;

    /**
     * Main constructor
     * 
     * @param schemaData 
     */
    public SchemaDataInfoPanel(ArrayList<SchemaData> aspaceSchemaDataList, SchemaData schemaData) {
        this.aspaceSchemaDataList = aspaceSchemaDataList;
        this.schemaData = schemaData;
    }

    /**
     * Method that is called automatically
     * @return 
     */
    public Widget asWidget() {
        VerticalLayoutContainer container = new VerticalLayoutContainer();

        IdentityValueProvider<SchemaDataField> identity = new IdentityValueProvider<SchemaDataField>();

        RowNumberer<SchemaDataField> numberer = new RowNumberer<SchemaDataField>(identity);

        ColumnConfig<SchemaDataField, String> nameCol = new ColumnConfig<SchemaDataField, String>(props.name(), 150, "Field Name");
        ColumnConfig<SchemaDataField, String> typeCol = new ColumnConfig<SchemaDataField, String>(props.type(), 150, "Type and Length");
        ColumnConfig<SchemaDataField, String> mappedToCol = new ColumnConfig<SchemaDataField, String>(props.mappedTo(), 250, "Mapped To");
        ColumnConfig<SchemaDataField, String> noteCol = new ColumnConfig<SchemaDataField, String>(props.note(), 400, "Mapping Note");

        List<ColumnConfig<SchemaDataField, ?>> list = new ArrayList<ColumnConfig<SchemaDataField, ?>>();
        list.add(numberer);
        list.add(nameCol);
        list.add(typeCol);
        list.add(mappedToCol);
        list.add(noteCol);

        ColumnModel<SchemaDataField> cm = new ColumnModel<SchemaDataField>(list);

        ListStore<SchemaDataField> store = new ListStore<SchemaDataField>(props.id());
        store.addAll(schemaData.getFields());

        grid = new Grid<SchemaDataField>(store, cm);
        grid.setBorders(true);
        grid.focus();
        grid.getView().setAutoExpandColumn(nameCol);
        grid.getView().setStripeRows(true);
        grid.getView().setColumnLines(true);

        numberer.initPlugin(grid);

        FramedPanel fp = new FramedPanel();
        fp.setHeadingText(schemaData.getName() + " Schema Fields");
        fp.setPixelSize(980, 350);
        fp.addStyleName("margin-10");
        fp.setWidget(grid);

        // add the grid to the container
        container.add(fp, new VerticalLayoutData(-1, -1, new Margins(4)));

        // TO-DO if the person is logged in then display this
        if (MainEntryPoint.loggedIn && schemaData.getType().equals(SchemaData.AT_TYPE)) {
            container.add(getEditPanel(grid), new VerticalLayoutData(-1, -1, new Margins(4)));
        }

        return container;
    }

    /**
     * Return the panel that allows editing mapping information
     * 
     * @return 
     */
    private ContentPanel getEditPanel(final Grid<SchemaDataField> grid) {
        // add the buttons that allow for entering the mapping information
        FramedPanel fp = new FramedPanel();
        fp.setHeadingText("Edit Mapping and Schema Information");
        fp.setPixelSize(980, 200);
        fp.addStyleName("margin-10");

        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        fp.add(vlc);

        SchemaDataProperties props = GWT.create(SchemaDataProperties.class);
        ListStore<SchemaData> store = new ListStore<SchemaData>(props.id());
        store.addSortInfo(new StoreSortInfo<SchemaData>(props.name(), SortDir.ASC));
        
        store.addAll(aspaceSchemaDataList);

        final ComboBox<SchemaData> combo = new ComboBox<SchemaData>(store, props.nameLabel());
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        vlc.add(new FieldLabel(combo, "ASpace Schema"), new VerticalLayoutData(1, -1));

        mapToTextField = new TextField();
        vlc.add(new FieldLabel(mapToTextField, "Mapped To"), new VerticalLayoutData(1, -1));

        noteTextField = new TextField();
        vlc.add(new FieldLabel(noteTextField, "Mapping Note"), new VerticalLayoutData(1, -1));

        TextArea noteTextArea = new TextArea();
        noteTextArea.setValue(schemaData.getNote());
        vlc.add(new FieldLabel(noteTextArea, "Schema Note"), new VerticalLayoutData(1, 100));

        // add the button to update it now
        TextButton viewFieldButton = new TextButton("View Selected ASpace Schema Fields", new SelectHandler() {

            public void onSelect(SelectEvent event) {
                SchemaData sdata = combo.getValue();

                if (sdata != null) {
                    displaySchemaFieldsWindow(sdata);
                }
            }
        });
        
        fp.addButton(viewFieldButton);
        
        // add the button to update it now
        TextButton copyFieldButton = new TextButton("Copy AT Schema Fields", new SelectHandler() {

            public void onSelect(SelectEvent event) {
                displayCopySchemaFieldsWindow();
            }
        });

        fp.addButton(copyFieldButton);
        
        // add the button to update it now
        TextButton importFieldButton = new TextButton("Import Fields Mapping", new SelectHandler() {

            public void onSelect(SelectEvent event) {
                displayImportSchemaFieldsWindow();
            }
        });

        fp.addButton(importFieldButton);

        // add button to update the field info to the schema field object
        SelectHandler sh = new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                SchemaDataField field = grid.getSelectionModel().getSelectedItem();

                if (field != null) {
                    String schemaName = combo.getValue().getName();
                    String fieldName = mapToTextField.getCurrentValue();

                    if (fieldName == null) {
                        fieldName = "{see schema}";
                    }

                    field.setMappedTo(schemaName + " -> " + fieldName);
                    
                    field.setNote(noteTextField.getCurrentValue());

                    grid.getView().refresh(true);

                    Info.display("Update", "Updated mapping Information for " + field.getName());
                }
            }
        };

        TextButton updateButton = new TextButton("Update");
        updateButton.addSelectHandler(sh);
        fp.addButton(updateButton);

        fp.addButton(new TextButton("Submit"));

        return fp;
    }

    /**
     * Method to display the window that show the schema fields
     */
    private void displaySchemaFieldsWindow(SchemaData sdata) {
        SchemaFieldsWindow window = new SchemaFieldsWindow(this, sdata);
        window.show();
    }
    
    /**
     * Method to display a window that allows copying the schema fields and information
     * in a text window
     * 
     * @param sdata 
     */
    private void displayCopySchemaFieldsWindow() {
        SchemaFieldsCopyWindow window = new SchemaFieldsCopyWindow(this, 
                SchemaFieldsCopyWindow.COPY_VIEW, schemaData);
        window.show();
    }
    
    /**
     * Method to display import the schema fields window, which allows quick import of data
     */
    private void displayImportSchemaFieldsWindow() {
        SchemaFieldsCopyWindow window = new SchemaFieldsCopyWindow(this, 
                SchemaFieldsCopyWindow.IMPORT_VIEW, schemaData);
        window.show();
    }

    /**
     * Method that called to set the text in the map to text field
     * 
     * @param fieldName 
     */
    public void updateMapToTextField(String schemaName, String fieldName) {
        mapToTextField.setValue(fieldName);

        SchemaDataField field = grid.getSelectionModel().getSelectedItem();
        
        if (field != null) {
            field.setMappedTo(schemaName + " -> " + fieldName);
            
            field.setNote(noteTextField.getCurrentValue());
            
            grid.getView().refresh(true);
        }
    }
}