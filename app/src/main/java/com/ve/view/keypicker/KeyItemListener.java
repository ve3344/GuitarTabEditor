package com.ve.view.keypicker;

public interface KeyItemListener {
	void onItemClick(KeyPicker keyPicker, int index);
	String onGetKeyText(KeyPicker keyPicker, int index);
}
