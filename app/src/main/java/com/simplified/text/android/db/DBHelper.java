package com.simplified.text.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.simplified.text.android.models.Example;
import com.simplified.text.android.models.HighlighterModel;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.NotesModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Base Class. Containing all the queries and functions related to DataBase
 * used.
 */
public class DBHelper extends SQLiteOpenHelper {
    static SQLiteDatabase dba;

    public static final String DATABASE_NAME = "SimplifiedText.db";
    public static final int DATABASE_VERSION = 1;


    static String word_table = "wordsTable";
    static String meaning_table = "meaningTable";
    static String pronunciation_table = "pronunciationsTable";
    static String example_table = "examplesTable";
    static String notes_table = "notesTable";
    static String notes_highlighter_table = "notesHighlighgterTable";


    static String word_id = "wordId";
    static String word = "word";


    static String meaning_id = "meaningId";
    static String headword = "headword";
    static String part_of_speech = "partOfSpeech";
    static String meaning = "meaning";


    static String pronunciation_id = "pronunciationId";
    static String lang = "lang";
    static String url = "url";
    static String ipa = "ipa";


    static String example_id = "exampleId";
    static String example = "example";
    static String audio_url = "audioUrl";


    static String notes_id = "notesId";
    static String notes = "notes";
    static String date = "date";
    static String isHtml = "isHtml";

    static String highlighter_id = "highlighterId";
    static String start_point = "startPoint";
    static String end_point = "end_point";
    static String highlighter_color = "highlighterColor";


    /*static String product_id = "productId";
    static String product_name = "productName";
    static String sku = "sku";
    static String label = "label";
    static String description = "description";
    static String value = "value";
    static String store_id = "storeId";
    static String mrp = "mrp";
    static String sp = "sp";
    static String imageUrl = "imageUrl";
    static String quantityInCart = "quantityInCart";*/


   /* static String notification_title = "notificationTitle";
    static String notification_date = "date";
    static String notification_message = "message";
    static String notification_status = "status";*/

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// Not Required but we have to override it
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        dba = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Not Required but we have to override it
    }

    public void CreateTable() {
      /*  dba.execSQL("Create table IF NOT EXISTS " + cartProductTable_table + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + product_id + " TEXT, " + product_name + " TEXT, " + sku + " TEXT, " + label + " " +
                "TEXT, "
                + description + " TEXT, " + value + " TEXT ," + store_id
                + " TEXT," + mrp + " TEXT," + sp + " TEXT,"
                + imageUrl + " TEXT," + quantityInCart + " INTEGER)");*/


        dba.execSQL("Create table IF NOT EXISTS " + word_table + "(" + word_id + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,"
                + word + " TEXT )");


        dba.execSQL("Create table IF NOT EXISTS " + meaning_table + "(" + meaning_id + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,"
                + headword + " TEXT, " + part_of_speech + " TEXT, " + meaning + " " +
                "TEXT, " + word_id +
                " TEXT )");


        dba.execSQL("Create table IF NOT EXISTS " + pronunciation_table + "(" + pronunciation_id + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,"
                + lang + " TEXT, " + url + " TEXT, " + ipa + " " +
                "TEXT, " + meaning_id + " " + "TEXT, " + word_id +
                " TEXT )");

        dba.execSQL("Create table IF NOT EXISTS " + example_table + "(" + example_id + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,"
                + example + " TEXT, " + audio_url + " TEXT, " + meaning_id + " " +
                "TEXT, " + word_id +
                " TEXT )");

        dba.execSQL("Create table IF NOT EXISTS " + notes_table + "(" + notes_id + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,"
                + notes + " TEXT, " + date + " TEXT, " + isHtml + " " +
                "TEXT )");


        dba.execSQL("Create table IF NOT EXISTS " + notes_highlighter_table + "(" + highlighter_id + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,"
                + notes_id + " TEXT, " + start_point + " INTEGER, " + end_point + " " + " INTEGER, " + highlighter_color +
                " INTEGER )");


    }

    public void insertNotes(NotesModel notesModel) {
        ContentValues notesTablevalues = new ContentValues();
        notesTablevalues.put(notes, notesModel.notes);
        notesTablevalues.put(date, notesModel.date);
        notesTablevalues.put(isHtml, notesModel.isHtml);
        dba.insert(notes_table, null, notesTablevalues);
    }

    public void insertNoteHighlighter(HighlighterModel highlighterModel) {
        ContentValues HighlighterTablevalues = new ContentValues();
        HighlighterTablevalues.put(notes_id, highlighterModel.noteId);
        HighlighterTablevalues.put(start_point, highlighterModel.startPoint);
        HighlighterTablevalues.put(end_point, highlighterModel.endPoint);
        HighlighterTablevalues.put(highlighter_color, highlighterModel.hColor);
        dba.insert(notes_highlighter_table, null, HighlighterTablevalues);
    }

    public void insertWordMeaning(MeaningModel meaningModel) {


        ContentValues wordTablevalues = new ContentValues();
        wordTablevalues.put(word, meaningModel.word.toLowerCase());
        long WORD_ID_TO_INSERT = dba.insert(word_table, null, wordTablevalues);


        if (meaningModel.results != null) {
            for (Result mMeaning : meaningModel.results) {
                ContentValues meaningTablevalues = new ContentValues();
                meaningTablevalues.put(headword, mMeaning.headword);
                meaningTablevalues.put(part_of_speech, mMeaning.part_of_speech);
                meaningTablevalues.put(meaning, mMeaning.meaning);
                meaningTablevalues.put(word_id, WORD_ID_TO_INSERT);

                long MEANING_ID_TO_INSERT = dba.insert(meaning_table, null, meaningTablevalues);

                if (mMeaning.pronunciations != null) {
                    for (Pronunciations pronunciation : mMeaning.pronunciations) {
                        ContentValues pronunciationTablevalues = new ContentValues();
                        pronunciationTablevalues.put(lang, pronunciation.lang);
                        pronunciationTablevalues.put(url, pronunciation.url);
                        pronunciationTablevalues.put(ipa, pronunciation.ipa);
                        pronunciationTablevalues.put(meaning_id, MEANING_ID_TO_INSERT);
                        pronunciationTablevalues.put(word_id, WORD_ID_TO_INSERT);

                        dba.insert(pronunciation_table, null, pronunciationTablevalues);
                    }
                }

                if (mMeaning.example != null) {
                    ContentValues exampleTablevalues = new ContentValues();
                    exampleTablevalues.put(example, mMeaning.example.example);
                    exampleTablevalues.put(audio_url, mMeaning.example.url);
                    exampleTablevalues.put(meaning_id, MEANING_ID_TO_INSERT);
                    exampleTablevalues.put(word_id, WORD_ID_TO_INSERT);
                    dba.insert(example_table, null, exampleTablevalues);
                }
            }
        }
    }


    public List<HighlighterModel> getHighlighterList(String noteId) {
        List<HighlighterModel> highlighterList = new ArrayList<HighlighterModel>();
        Cursor highlighterTableCursor = null;

        highlighterTableCursor = dba.rawQuery("SELECT * FROM " + notes_highlighter_table + " where " +
                        notes_id + " = '" + noteId + "' order" +
                        " by " + highlighter_id + " ASC",
                null);


        if (highlighterTableCursor.moveToFirst()) {
            do {
                HighlighterModel highlighterModel = new HighlighterModel();

                highlighterModel.highlighterId = highlighterTableCursor.getString(highlighterTableCursor.getColumnIndex(highlighter_id));
                highlighterModel.noteId = highlighterTableCursor.getString(highlighterTableCursor.getColumnIndex(notes_id));
                highlighterModel.startPoint = highlighterTableCursor.getInt(highlighterTableCursor.getColumnIndex(start_point));
                highlighterModel.endPoint = highlighterTableCursor.getInt(highlighterTableCursor.getColumnIndex(end_point));
                highlighterModel.hColor = highlighterTableCursor.getInt(highlighterTableCursor.getColumnIndex(highlighter_color));

                highlighterList.add(highlighterModel);
            } while (highlighterTableCursor.moveToNext());
        }

        highlighterTableCursor.close();

        return highlighterList;
    }


    public List<NotesModel> getNotesList(String searchKey) {
        List<NotesModel> meaningList = new ArrayList<NotesModel>();
        Cursor noteTableCursor = null;
        if (TextUtils.isEmpty(searchKey)) {
            noteTableCursor = dba.rawQuery("SELECT * FROM " + notes_table + "  order" +
                            " by " + notes_id + " DESC",
                    null);
        } else {
            noteTableCursor = dba.rawQuery("SELECT * FROM " + notes_table + " where " +
                            notes + " like '" +"%"+ searchKey+"%" + "' order" +
                            " by " + notes_id + " DESC",
                    null);
        }


        if (noteTableCursor.moveToFirst()) {
            do {
                NotesModel notesModel = new NotesModel();

                notesModel.notesId = noteTableCursor.getString(noteTableCursor.getColumnIndex(notes_id));
                notesModel.notes = noteTableCursor.getString(noteTableCursor.getColumnIndex(notes));
                notesModel.date = noteTableCursor.getString(noteTableCursor.getColumnIndex(date));
                notesModel.isHtml = noteTableCursor.getString(noteTableCursor.getColumnIndex(isHtml));

                meaningList.add(notesModel);
            } while (noteTableCursor.moveToNext());
        }

        noteTableCursor.close();

        return meaningList;
    }

    public void removeNote(String noteId) {
        dba.execSQL("DELETE FROM " + notes_table + " WHERE " + notes_id + " = " + Long.parseLong(noteId) + "");
    }

    public void removeNoteHighlighters(String noteId) {
        dba.execSQL("DELETE FROM " + notes_highlighter_table + " WHERE " + notes_id + " = " + Long.parseLong(noteId) + "");
    }

    public void removeNoteHighlighter(String highlighterId) {

        dba.execSQL("DELETE FROM " + notes_highlighter_table + " WHERE " + highlighter_id + " = " + Long.parseLong(highlighterId) + "");
    }

    public List<MeaningModel> getWordMeaningList(String mWord) {
        List<MeaningModel> meaningList = new ArrayList<MeaningModel>();
        try {

            Cursor wordTableCursor = null;
            if (TextUtils.isEmpty(mWord)) {
                wordTableCursor = dba.rawQuery("SELECT * FROM " + word_table + "  order" +
                                " by " + word_id + " DESC",
                        null);
            } else {
                wordTableCursor = dba.rawQuery("SELECT * FROM " + word_table + " where " +
                                word + " = '" + mWord + "' order" +
                                " by " + word_id + " ASC",
                        null);
            }

            if (wordTableCursor.moveToFirst()) {
                do {
                    meaningList.add(setMeaningModel(wordTableCursor));
                } while (wordTableCursor.moveToNext());
            }
            wordTableCursor.close();
        } catch (Exception ignored) {
        }
        return meaningList;
    }


    public List<MeaningModel> searchMeaningList(String mWord) {
        List<MeaningModel> meaningList = new ArrayList<MeaningModel>();
        try {
            Cursor wordTableCursor = null;
                wordTableCursor = dba.rawQuery("SELECT * FROM " + word_table + " where " +
                                word + " like '" +mWord+"%" + "' order" +
                                " by " + word_id + " DESC",
                        null);
            if (wordTableCursor.moveToFirst()) {
                do {
                    meaningList.add(setMeaningModel(wordTableCursor));
                } while (wordTableCursor.moveToNext());
            }
            wordTableCursor.close();
        } catch (Exception ignored) {
        }
        return meaningList;
    }


    private MeaningModel setMeaningModel(Cursor wordTableCursor) {
        MeaningModel meaningModel = new MeaningModel();
        meaningModel.id = String.valueOf(wordTableCursor.getInt(wordTableCursor.getColumnIndex(word_id)));
        meaningModel.word = wordTableCursor.getString(wordTableCursor.getColumnIndex(word));

        Cursor meaningTableCursor = dba.rawQuery("SELECT * FROM " + meaning_table + " where " +
                        word_id + " = '" + meaningModel.id + "' order" +
                        " by " + meaning_id + " ASC",
                null);

        if (meaningTableCursor.moveToFirst()) {
            ArrayList<Result> resultListList = new ArrayList<Result>();
            do {
                Result meaningResult = new Result();
                meaningResult.id = String.valueOf(meaningTableCursor.getInt(meaningTableCursor.getColumnIndex(meaning_id)));
                meaningResult.headword = meaningTableCursor.getString(meaningTableCursor.getColumnIndex(headword));
                meaningResult.part_of_speech = meaningTableCursor.getString(meaningTableCursor.getColumnIndex(part_of_speech));
                meaningResult.meaning = meaningTableCursor.getString(meaningTableCursor.getColumnIndex(meaning));


                Cursor exampleTableCursor = dba.rawQuery("SELECT * FROM " + example_table + " where " +
                                meaning_id + " = '" + meaningResult.id + "' order" +
                                " by " + example_id + " ASC",
                        null);
                if (exampleTableCursor.moveToFirst()) {
                    Example exampleModel = new Example();
                    exampleModel.id = String.valueOf(exampleTableCursor.getInt(exampleTableCursor.getColumnIndex(example_id)));
                    exampleModel.example = exampleTableCursor.getString(exampleTableCursor.getColumnIndex(example));
                    exampleModel.url = exampleTableCursor.getString(exampleTableCursor.getColumnIndex(audio_url));
                    meaningResult.example = exampleModel;
                }
                exampleTableCursor.close();


                Cursor PronunciationTableCursor = dba.rawQuery("SELECT * FROM " + pronunciation_table + " where " +
                                meaning_id + " = '" + meaningResult.id + "' order" +
                                " by " + pronunciation_id + " ASC",
                        null);
                if (PronunciationTableCursor.moveToFirst()) {
                    ArrayList<Pronunciations> pronunciationList = new ArrayList<>();
                    do {

                        Pronunciations pronunciationsModel = new Pronunciations();
                        pronunciationsModel.id = String.valueOf(PronunciationTableCursor.getInt(PronunciationTableCursor.getColumnIndex(pronunciation_id)));
                        pronunciationsModel.ipa = PronunciationTableCursor.getString(PronunciationTableCursor.getColumnIndex(ipa));
                        pronunciationsModel.lang = PronunciationTableCursor.getString(PronunciationTableCursor.getColumnIndex(lang));
                        pronunciationsModel.url = PronunciationTableCursor.getString(PronunciationTableCursor.getColumnIndex(url));
                        pronunciationList.add(pronunciationsModel);
                    } while (PronunciationTableCursor.moveToNext());
                    meaningResult.pronunciations = pronunciationList;
                }

                PronunciationTableCursor.close();


                resultListList.add(meaningResult);
            } while (meaningTableCursor.moveToNext());
            meaningModel.results = resultListList;
        }

        meaningTableCursor.close();

        return meaningModel;
    }


    public void removeWord(String wordId) {
        dba.execSQL("DELETE FROM " + word_table + " WHERE " + word_id + " = " + Long.parseLong(wordId) + "");
        dba.execSQL("DELETE FROM " + meaning_table + " WHERE " + word_id + " = " + Long.parseLong(wordId) + "");
        dba.execSQL("DELETE FROM " + pronunciation_table + " WHERE " + word_id + " = " + Long.parseLong(wordId) + "");
        dba.execSQL("DELETE FROM " + example_table + " WHERE " + word_id + " = " + Long.parseLong(wordId) + "");
    }


   /* public void insertNotificationItem(String title, String message, String createdDate) {

        dba.execSQL("INSERT INTO " + notification_table + "(" + notification_title + "," +
                notification_message + "," + notification_date + ","
                + notification_status + ") VALUES ('" + title.replaceAll("'",
                "''") + "','" +
                message.replaceAll("'",
                        "''") + "','" + createdDate.replaceAll("'",
                "''") + "','0')");


    }*/


    /*public int getNewNotificationCount() {

        try {
            Cursor cursor = dba.rawQuery("SELECT * FROM " + notification_table + " where " +
                            notification_status + " = '0' order" +
                            " by id DESC",
                    null);
            return cursor.getCount();
        } catch (Exception ignored) {
            AppUtils.logException(ignored);
        }

        return 0;
    }*/


   /* public List<NotificationModel> getNotifications() {
        List<NotificationModel> notificationList = new ArrayList<NotificationModel>();
        try {
            Cursor cursor = dba.rawQuery("SELECT * FROM " + notification_table + "  order" +
                            " by id DESC",
                    null);
            if (cursor.moveToFirst()) {
                do {
                    notificationList.add(new NotificationModel(cursor.getString(cursor
                            .getColumnIndex(notification_title)), cursor.getString(cursor.getColumnIndex(notification_message)), cursor.getString(cursor.getColumnIndex(notification_date))));


                } while (cursor.moveToNext());
            }
        } catch (Exception ignored) {
            AppUtils.logException(ignored);
        }
        updateNotificationStatus();
        return notificationList;
    }*/

   /* private void updateNotificationStatus() {
        dba.execSQL("UPDATE " + notification_table + " SET " + notification_status + " = " + "'1'");
    }*/


  /*  public void insertCartItem(CategoryProductsModel.ProductItemModel product) {
        String tName = "";
        String tValue = "";
        String tLabel = "";
        String tMrp = "";
        String tSp = "", tDescription = "", tStoreId = "", tImageUrl = "", tSku = "";

        if (product.name != null) {
            tName = product.name.replace("'",
                    "''");
        }

        if (product.sku != null) {
            tSku = product.sku.replace("'",
                    "''");
        }

        if (product.attributes != null && product.attributes.get(0) != null && product.attributes
                .get(0).name != null && "Quantity".equalsIgnoreCase(product.attributes
                .get(0).name) &&
                product
                        .attributes.get(0).value != null) {
            tValue = product.attributes.get(0).value.replace("'",
                    "''");
        }
        if (product.label != null) {
            tLabel = product.label.replace("'",
                    "''");

        }
        if (product.description != null) {
            tDescription = product.description.replace("'",
                    "''");

        }

        if (product.itemStoreSpecificValues != null && product.itemStoreSpecificValues.get(0)
                != null && product.itemStoreSpecificValues.get(0).productMrp != null) {
            tMrp = product.itemStoreSpecificValues.get(0).productMrp.replace("'",
                    "''");
        }


        if (product.itemStoreSpecificValues != null && product.itemStoreSpecificValues.get(0)
                != null && product.itemStoreSpecificValues.get(0).productSp != null) {
            tSp = product.itemStoreSpecificValues.get(0)
                    .productSp.replace("'",
                            "''");
        }
        if (product.itemStoreSpecificValues != null && product.itemStoreSpecificValues.get(0)
                != null && product.itemStoreSpecificValues.get(0).storeProductId != null) {
            tStoreId = product.itemStoreSpecificValues.get(0)
                    .storeProductId.replace("'",
                            "''");
        }

        if (product.imageList != null && product.imageList.images != null && product.imageList.images.get(0)
                != null && product.imageList.images.get(0).src != null) {
            tImageUrl = product.imageList.images.get(0).src.replace("'",
                    "''");
        }

        dba.execSQL("INSERT INTO " + cartProductTable_table + "(" + product_id + "," +
                product_name + "," + sku + ","
                + label + "," + description + "," + value + ", "
                + store_id + ", " + mrp + ", " + sp + ", "
                + imageUrl + ", " + quantityInCart
                + ") VALUES ('" + product.productId.id.replaceAll("'", "''") + "','" +
                tName + "','" + tSku + "','"
                + tLabel + "','" + tDescription + "','"
                + tValue +
                "','"
                + tStoreId + "','" + tMrp
                +
                "','"
                + tSp + "','" + tImageUrl
                + "'," + product.quantityInCart + ")");
    }*/

    /*public void removeCartItem(CategoryProductsModel.ProductItemModel cartProduct) {
        dba.execSQL("DELETE FROM " + cartProductTable_table + " WHERE " + product_id + " = '" + cartProduct.productId.id + "'");
    }*/

   /* public void removeAllCartItems() {
        dba.execSQL("DELETE FROM " + cartProductTable_table);
    }*/

   /* public void updateCartItemCount(CategoryProductsModel.ProductItemModel cartProduct) {
        dba.execSQL("UPDATE " + cartProductTable_table + " SET " + quantityInCart + "= " + cartProduct.quantityInCart + " WHERE " + product_id + "='"
                + cartProduct.productId.id + "';");
    }*/

   /* public void updateCartItemPrice(CategoryProductsModel.ProductItemModel product) {
        String tMrp = "";
        String tSp = "";

        if (product.itemStoreSpecificValues != null && product.itemStoreSpecificValues.get(0)
                != null && product.itemStoreSpecificValues.get(0).productMrp != null) {
            tMrp = product.itemStoreSpecificValues.get(0).productMrp.replace("'",
                    "''");
        }


        if (product.itemStoreSpecificValues != null && product.itemStoreSpecificValues.get(0)
                != null && product.itemStoreSpecificValues.get(0).productSp != null) {
            tSp = product.itemStoreSpecificValues.get(0)
                    .productSp.replace("'",
                            "''");
        }

        dba.execSQL("UPDATE " + cartProductTable_table + " SET " + mrp + "= '" + tMrp + "', " +
                sp + " = '" + tSp +
                "' WHERE " +
                product_id + "='"
                + product.productId.id + "';");
    }*/


   /* public ArrayList<CategoryProductsModel.ProductItemModel> getCartItems() {
        ArrayList<CategoryProductsModel.ProductItemModel> mCartList = new ArrayList<>();

        Cursor cursor = dba.rawQuery("SELECT * FROM " + cartProductTable_table + " order by id DESC",
                null);
        CategoryProductsModel categoryProductsModel = new CategoryProductsModel();
        if (cursor.moveToFirst()) {
            do {

                CategoryProductsModel.ProductItemModel cartItem = categoryProductsModel.new ProductItemModel();

                CategoryProductsModel.IdC productId =
                        categoryProductsModel.new IdC();
                productId.id = cursor.getString(cursor.getColumnIndex(product_id));

                cartItem.productId = productId;
                cartItem.name = cursor.getString(cursor.getColumnIndex(product_name));
                cartItem.sku = cursor.getString(cursor.getColumnIndex(sku));
                cartItem.label = cursor.getString(cursor.getColumnIndex(label));
                cartItem.description = cursor.getString(cursor.getColumnIndex(description));
                cartItem.quantityInCart = cursor.getInt(cursor.getColumnIndex(quantityInCart));

                List<CategoryProductsModel.AttributesModel> attributesModels = new
                        ArrayList<CategoryProductsModel.AttributesModel>();
                CategoryProductsModel.AttributesModel attribute = categoryProductsModel.new
                        AttributesModel();
                attribute.name = "Quantity";
                attribute.value = cursor.getString(cursor.getColumnIndex(value));

                attributesModels.add(attribute);
                cartItem.attributes = attributesModels;


                CategoryProductsModel.ImageModel imageModel = categoryProductsModel.new ImageModel();
                List<CategoryProductsModel.SrcC> imageList = new
                        ArrayList<CategoryProductsModel.SrcC>();
                CategoryProductsModel.SrcC src = categoryProductsModel.new SrcC();
                src.src = cursor.getString(cursor.getColumnIndex(imageUrl));
                imageList.add(src);
                imageModel.images = imageList;
                cartItem.imageList = imageModel;


                CategoryProductsModel.ItemStoreSpecificValues storeSpecificValues = categoryProductsModel.new
                        ItemStoreSpecificValues();
                List<CategoryProductsModel.ItemStoreSpecificValues> itemStoreSpecificValues = new
                        ArrayList<CategoryProductsModel.ItemStoreSpecificValues>();
                storeSpecificValues.productMrp = cursor.getString(cursor.getColumnIndex(mrp));
                storeSpecificValues.productSp = cursor.getString(cursor.getColumnIndex(sp));
                storeSpecificValues.storeProductId = cursor.getString(cursor.getColumnIndex(store_id));
                itemStoreSpecificValues.add(storeSpecificValues);
                cartItem.itemStoreSpecificValues = itemStoreSpecificValues;

                mCartList.add(cartItem);
            } while (cursor.moveToNext());
        }


        return mCartList;
    }*/


}
