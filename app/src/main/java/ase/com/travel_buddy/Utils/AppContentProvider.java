package ase.com.travel_buddy.Utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import ase.com.travel_buddy.Database.MomentDAO;
import ase.com.travel_buddy.Database.TravelBuddyDatabase;
import ase.com.travel_buddy.Models.Moment;

public class AppContentProvider extends ContentProvider {
    public static final String AUTHORITY = "ase.com.travel_buddy.Utils";

    public static final Uri URI_MOMENTS = Uri.parse(
            "content://" + AUTHORITY + "/moments"
    );

    private static final int ROUTE_MOMENTS = 1;
    private static final int ROUTE_MOMENTS_ID = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, "moments", ROUTE_MOMENTS);
        MATCHER.addURI(AUTHORITY, "moments/*", ROUTE_MOMENTS_ID);
    }

    public AppContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final Context context = getContext();
        if (context == null) {
            return 0;
        }
        int count;
        switch (MATCHER.match(uri)) {
            case ROUTE_MOMENTS:
                count = TravelBuddyDatabase.getInstance(context).getMomentDAO().delete();
                return count;
            case ROUTE_MOMENTS_ID:
                count = TravelBuddyDatabase.getInstance(context).getMomentDAO()
                        .deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match =  MATCHER.match(uri);
        switch (match) {
            case ROUTE_MOMENTS:
                return "vnd.android.cursor.dir/" + AUTHORITY + ".moments";
            case ROUTE_MOMENTS_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + ".moments";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
       final int match = MATCHER.match(uri);
       switch (match) {
           case ROUTE_MOMENTS:
               final Context context = getContext();
               if (context == null) {
                   return null;
               }
               final Long id = TravelBuddyDatabase.getInstance(context).getMomentDAO().insertFromContentValues(Moment.fromContentValues(values));
               context.getContentResolver().notifyChange(uri, null);
               return ContentUris.withAppendedId(uri, id);
           case ROUTE_MOMENTS_ID:
               throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
           default:
               throw new IllegalArgumentException("Unknown URI: " + uri);
       }
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final int matcher = MATCHER.match(uri);
        if (matcher == ROUTE_MOMENTS || matcher == ROUTE_MOMENTS_ID) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            MomentDAO momentDAO = TravelBuddyDatabase.getInstance(context).getMomentDAO();
            final Cursor cursor;
            if (matcher == ROUTE_MOMENTS) {
                cursor = momentDAO.getMomentListCursor();
            } else {
                cursor = momentDAO.getMomentByIdCursor(ContentUris.parseId(uri));
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
