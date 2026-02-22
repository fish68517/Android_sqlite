package com.example.healthdietapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.fragments.CollectionRecipeFragment;
import com.example.healthdietapp.fragments.CollectionPostFragment;
import com.example.healthdietapp.fragments.CollectionQuestionFragment;

/**
 * CollectionPagerAdapter - Adapter for ViewPager2 in MyCollectionsActivity
 */
public class CollectionPagerAdapter extends FragmentStateAdapter {

    private String userId;
    private PostDAO postDAO;
    private RecipeDAO recipeDAO;

    public CollectionPagerAdapter(@NonNull FragmentActivity fragmentActivity, String userId, PostDAO postDAO, RecipeDAO recipeDAO) {
        super(fragmentActivity);
        this.userId = userId;
        this.postDAO = postDAO;
        this.recipeDAO = recipeDAO;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        /*switch (position) {
            case 0:
                return CollectionRecipeFragment.newInstance(userId, recipeDAO);
            case 1:
                return CollectionPostFragment.newInstance(userId, postDAO);
            case 2:
                return CollectionQuestionFragment.newInstance(userId);
            default:
                return new Fragment();
        }*/

        switch (position) {
            case 0:
                return CollectionPostFragment.newInstance(userId, postDAO);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
