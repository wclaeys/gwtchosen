/*
 * Copyright (C) 2012 Julien Dramaix
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.watopi.chosen.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.query.client.js.JsObjectArray;

public class SelectParser {

  protected abstract class SelectItem {
    protected boolean disabled;
    protected int arrayIndex;
    protected String domId;

    public abstract boolean isGroup();

    public boolean isDisabled() {
      return disabled;
    }

    public int getArrayIndex() {
      return arrayIndex;
    }

    public String getDomId() {
      return domId;
    }

    public void setDomId(String domId) {
      this.domId = domId;
    }

    public boolean isEmpty() {
      return false;
    }

  }

  protected class GroupItem extends SelectItem {

    private String label;
    private int children = 0;

    @Override
    public boolean isGroup() {
      return true;
    }

    public String getLabel() {
      return label;
    }

    public int getChildren() {
      return children;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public void setChildren(int children) {
      this.children = children;
    }
  }

  protected class OptionItem extends SelectItem {

    public void setArrayIndex(int arrayIndex) {
      this.arrayIndex = arrayIndex;
    }

    public void setOptionsIndex(int optionsIndex) {
      this.optionsIndex = optionsIndex;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public void setText(String text) {
      this.text = text;
    }

    public void setHtml(String html) {
      this.html = html;
    }

    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    public void setDisabled(boolean disabled) {
      this.disabled = disabled;
    }

    public void setGroupArrayIndex(int groupArrayIndex) {
      this.groupArrayIndex = groupArrayIndex;
    }

    public void setClasses(String classes) {
      this.classes = classes;
    }

    public void setStyle(String style) {
      this.style = style;
    }

    public void setEmpty(boolean empty) {
      this.empty = empty;
    }

    private int arrayIndex;
    private int optionsIndex;
    private String value;
    private String text;
    private String html;
    private boolean selected;
    private boolean disabled;
    private int groupArrayIndex;
    private String classes;
    private String style;
    private boolean empty;

    public int getArrayIndex() {
      return arrayIndex;
    }

    public int getOptionsIndex() {
      return optionsIndex;
    }

    public String getValue() {
      return value;
    }

    public String getText() {
      return text;
    }

    public String getHtml() {
      return html;
    }

    public boolean isSelected() {
      return selected;
    }

    public boolean isDisabled() {
      return disabled;
    }

    public int getGroupArrayIndex() {
      return groupArrayIndex;
    }

    public String getClasses() {
      return classes;
    }

    public String getStyle() {
      return style;
    }

    public boolean isEmpty() {
      return empty;
    }

    @Override
    public boolean isGroup() {
      return false;
    }
  }

  private int optionsIndex;
  private JsObjectArray<SelectItem> parsed;

  public SelectParser() {
    optionsIndex = 0;
    parsed = JsObjectArray.create();

  }

  private void addNode(Node child) {
    if (!Element.is(child)) {
      return;
    }

    Element e = Element.as(child);

    if ("OPTGROUP".equalsIgnoreCase(e.getNodeName())) {
      addGroup(OptGroupElement.as(e));
    } else if ("OPTION".equalsIgnoreCase(e.getNodeName())) {
      addOption(OptionElement.as(e), -1, false);
    }
  }

  private void addGroup(OptGroupElement group) {
    int position = parsed.length();

    GroupItem item = new GroupItem();
    item.arrayIndex = position;
    item.label = group.getLabel();
    item.children = 0;
    item.disabled = group.isDisabled();

    parsed.add(item);

    NodeList<Node> children = group.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node n = children.getItem(i);
      if ("OPTION".equalsIgnoreCase(n.getNodeName())) {
        addOption(OptionElement.as((Element) n), position, group.isDisabled());
      }
    }

  }

  private void addOption(OptionElement option, int groupPosition, boolean groupDisabled) {
    String optionText = option.getText();

    OptionItem item = new OptionItem();
    item.arrayIndex = parsed.length();
    item.optionsIndex = optionsIndex;

    if (optionText != null && optionText.length() > 0) {

      if (groupPosition != -1) {
        ((GroupItem) parsed.get(groupPosition)).children++;
      }

      item.value = option.getValue();
      item.text = option.getText();
      item.html = option.getInnerHTML();
      item.selected = option.isSelected();
      item.disabled = groupDisabled ? groupDisabled : option.isDisabled();
      item.groupArrayIndex = groupPosition;
      item.classes = option.getClassName();
      item.style = getCssText(option.getStyle());
      item.empty = false;

    } else {
      item.empty = true;
      item.groupArrayIndex = -1;

    }

    parsed.add(item);
    optionsIndex++;
  }

  public JsObjectArray<SelectItem> parse(SelectElement select) {

    NodeList<Node> children = select.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node n = children.getItem(i);
      addNode(n);
    }

    return parsed;
  }

  private native String getCssText(Style s)/*-{
		return s.cssText;
	}-*/;
}
