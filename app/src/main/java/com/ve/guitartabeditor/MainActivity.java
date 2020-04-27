package com.ve.guitartabeditor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ve.view.FadeAnimation;
import com.ve.view.editor.Cursor;
import com.ve.view.editor.GuitarTab;
import com.ve.view.editor.Note;
import com.ve.view.editor.TabEditor;
import com.ve.view.keypicker.KeyItemListener;
import com.ve.view.keypicker.KeyPicker;


public class MainActivity extends AppCompatActivity implements TabEditor.CursorListener, KeyItemListener {

    private GuitarTab tab;
    private TabEditor tabEditor;
    private KeyPicker numberKeyPicker, addNoteKeyPicker, chordKeyPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }



    public static final String[] addKeyText = new String[]{"←", "→", "↑", "↓"};

    public void changeTab(GuitarTab tab) {
        this.tab = tab;
    }

    public GuitarTab getTab() {
        return tab;
    }

    @Override
    public void onItemClick(KeyPicker keyPicker, int index) {
        if (keyPicker == this.numberKeyPicker) {
            tabEditor.setCursorLine(index - 1);
        } else if (keyPicker == chordKeyPicker) {
            tabEditor.setCursorChrod(index);
        } else {
            switch (index) {
                case 0:
                    tabEditor.addNoteBeforeCursor(new Note());
                    break;
                case 1:
                    tabEditor.addNoteAfterCursor(new Note());
                    break;
                case 2:
                    tabEditor.addRowBeforeCursor();
                    break;
                case 3:
                    tabEditor.addRowAfterCursor();
                    break;
                case 4:
                    tabEditor.deleteNote();
                    break;
                case 5:
                    tabEditor.getTab().save();
                    break;
            }

        }
    }


    @Override
    public String onGetKeyText(KeyPicker keyPicker, int index) {
        if (keyPicker == this.numberKeyPicker) {
            if (index == 0) {
                return "－";
            } else if (index == 1) {
                return "X";
            } else if (index < 22) {
                return "" + (index - 1);
            }
        } else if (keyPicker == chordKeyPicker) {
            if (index >= 0 && index < GuitarTab.Chord.values().length) {
                return GuitarTab.Chord.values()[index].toString();
            } else {
                return " ";
            }

        } else {
            if (index < addKeyText.length) {
                return addKeyText[index];
            } else if (index == addKeyText.length) {
                return "删除";
            } else {
                return "保存";
            }
        }
        return " ";
    }


    @Override
    public void onSkip(Cursor cursor, Note note) {
        if (cursor.isInChord()) {
            chordKeyPicker.setSelectItem(tabEditor.getCursorChord().ordinal());
        } else {
            int line = note.line[cursor.getLine()];
            if (line > 20) {
                line = -1;
            }
            numberKeyPicker.setSelectItem(line + 1);
        }

    }

    @Override
    public void onChangeMode(boolean inChord) {
        if (inChord) {
            FadeAnimation.fadeTo(numberKeyPicker, chordKeyPicker);
        } else {
            FadeAnimation.fadeTo(chordKeyPicker, numberKeyPicker);
        }
    }

    private void init() {
        tabEditor = findViewById(R.id.TabEdit);
        tabEditor.setCursorListener(this);
        tabEditor.setTab(tab);
        numberKeyPicker = findViewById(R.id.NumberKeyPicker);
        numberKeyPicker.setItemListener(this);
        numberKeyPicker.setBody(2, 11);
        numberKeyPicker.setSelectable(true);

        chordKeyPicker = findViewById(R.id.ChordKeyPicker);
        chordKeyPicker.setItemListener(this);
        chordKeyPicker.setBody(2, 8);
        chordKeyPicker.setSelectable(true);

        addNoteKeyPicker = findViewById(R.id.NoteKeyPicker);
        addNoteKeyPicker.setItemListener(this);
        addNoteKeyPicker.setBody(1, 6);
        addNoteKeyPicker.setSelectable(false);
        //Toast.makeText(getContext(),"start" ,Toast.LENGTH_SHORT).show();
    }

}
