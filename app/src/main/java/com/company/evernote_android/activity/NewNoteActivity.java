package com.company.evernote_android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.evernote_android.R;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;

import java.util.List;


public class NewNoteActivity extends SessionHolder {
    private static final String LOGTAG = "NewNoteActivity";

    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private Button mBtnSave;
    private Button mBtnSelect;

    private String mSelectedNotebookGuid;

    private OnClientCallback<Note> mNoteCreateCallback = new OnClientCallback<Note>() {
        @Override
        public void onSuccess(Note note) {
            Toast.makeText(getApplicationContext(), R.string.note_saved, Toast.LENGTH_LONG).show();
            removeDialog(DIALOG_PROGRESS);
        }

        @Override
        public void onException(Exception exception) {
            Log.e(LOGTAG, "Error saving note", exception);
            Toast.makeText(getApplicationContext(), R.string.error_saving_note, Toast.LENGTH_LONG).show();
            removeDialog(DIALOG_PROGRESS);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        mEditTextTitle = (EditText) findViewById(R.id.text_title);
        mEditTextContent = (EditText) findViewById(R.id.text_content);
        mBtnSelect = (Button) findViewById(R.id.select_button);
        mBtnSave = (Button) findViewById(R.id.save_button);
    }

    /**
     * Saves text field content as note to selected notebook, or default notebook if no notebook select
     */
    public void saveNote(View view) {
        String title = mEditTextTitle.getText().toString();
        String content = mEditTextContent.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(getApplicationContext(), R.string.empty_content_error, Toast.LENGTH_LONG).show();
            return;
        }

        Note note = new Note();
        note.setTitle(title);

        //TODO: line breaks need to be converted to render in ENML
        note.setContent(EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX);

        if(!mEvernoteSession.getAuthenticationResult().isAppLinkedNotebook()) {
            //If User has selected a notebook guid, assign it now
            if (!TextUtils.isEmpty(mSelectedNotebookGuid)) {
                note.setNotebookGuid(mSelectedNotebookGuid);
            }
            showDialog(DIALOG_PROGRESS);
            try {
                mEvernoteSession.getClientFactory().createNoteStoreClient().createNote(note, mNoteCreateCallback);
            } catch (TTransportException exception) {
                Log.e(LOGTAG, "Error creating notestore", exception);
                Toast.makeText(getApplicationContext(), R.string.error_creating_notestore, Toast.LENGTH_LONG).show();
                removeDialog(DIALOG_PROGRESS);
            }
        } else {
            super.createNoteInAppLinkedNotebook(note, mNoteCreateCallback);
        }
    }


    /**
     * Select notebook, create AlertDialog to pick notebook guid
     */
    public void selectNotebook(View view) {
        if(mEvernoteSession.isAppLinkedNotebook()) {
            Toast.makeText(getApplicationContext(), getString(R.string.CANT_LIST_APP_LNB), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            mEvernoteSession.getClientFactory().createNoteStoreClient().listNotebooks(new OnClientCallback<List<Notebook>>() {
                int mSelectedPos = -1;

                @Override
                public void onSuccess(final List<Notebook> notebooks) {
                    CharSequence[] names = new CharSequence[notebooks.size()];
                    int selected = -1;
                    Notebook notebook = null;
                    for (int index = 0; index < notebooks.size(); index++) {
                        notebook = notebooks.get(index);
                        names[index] = notebook.getName();
                        if (notebook.getGuid().equals(mSelectedNotebookGuid)) {
                            selected = index;
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewNoteActivity.this);

                    builder
                            .setSingleChoiceItems(names, selected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSelectedPos = which;
                                }
                            })
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mSelectedPos > -1) {
                                        mSelectedNotebookGuid = notebooks.get(mSelectedPos).getGuid();
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(LOGTAG, "Error listing notebooks", exception);
                    Toast.makeText(getApplicationContext(), R.string.error_listing_notebooks, Toast.LENGTH_LONG).show();
                    removeDialog(DIALOG_PROGRESS);
                }
            });
        } catch (TTransportException exception) {
            Log.e(LOGTAG, "Error creating notestore", exception);
            Toast.makeText(getApplicationContext(), R.string.error_creating_notestore, Toast.LENGTH_LONG).show();
            removeDialog(DIALOG_PROGRESS);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
