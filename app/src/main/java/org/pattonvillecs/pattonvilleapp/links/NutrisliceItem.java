package org.pattonvillecs.pattonvilleapp.links;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.pattonvillecs.pattonvilleapp.DataSource;

/**
 * Created by Mitchell on 5/28/2017.
 */

public class NutrisliceItem extends SchoolLinkItem {
    public NutrisliceItem(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void onClick(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dataSource.nutrisliceLink.get())));
    }
}
