package com.example.busreservationsystem.Helper;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.busreservationsystem.Activities.Login;
import com.example.busreservationsystem.Activities.Profile;
import com.example.busreservationsystem.Activities.Signup;
import com.example.busreservationsystem.Activities.Trips;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class DrawerUtil {
    public static void getDrawer(Activity activity, Toolbar toolbar){
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem drawerEmptyItem= new PrimaryDrawerItem().withIdentifier(0).withName("");
        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem logout = new PrimaryDrawerItem()
                    .withIdentifier(1).withName(R.string.logout).withIcon(R.drawable.logout);
        PrimaryDrawerItem profile = new PrimaryDrawerItem().withIdentifier(2)
                .withName(R.string.my_profile).withIcon(R.drawable.profile);


        SecondaryDrawerItem main = new SecondaryDrawerItem().withIdentifier(3)
                .withName(R.string.main).withIcon(R.drawable.main);
        SecondaryDrawerItem trips = new SecondaryDrawerItem().withIdentifier(4)
                .withName(R.string.trips).withIcon(R.drawable.trips);






        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withSliderBackgroundDrawableRes(R.drawable.tripphoto)
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        drawerEmptyItem,drawerEmptyItem,
                        logout,
                        profile,
                        //main,
                        trips
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1) {//logout
                            Helper.removeToken(activity);
                            activity.startActivity(new Intent(activity, MainActivity.class));
                        }
                        else if (drawerItem.getIdentifier() == 2) {//profile
                            Intent intent = new Intent(activity, Profile.class);
                            intent.putExtra("token", Helper.loadToken(activity));
                            activity.startActivity(intent);
                        }


                        else if (drawerItem.getIdentifier() == 3) {//main
                            Helper.removeToken(activity);
                            activity.startActivity(new Intent(activity, MainActivity.class));
                        }
                        else if (drawerItem.getIdentifier() == 4) {//trips
                            Intent intent = new Intent(activity, Trips.class);
                            intent.putExtra("token", Helper.loadToken(activity));
                            activity.startActivity(intent);
                        }
                        return true;
                    }

                } )
                .build();
    }
}
