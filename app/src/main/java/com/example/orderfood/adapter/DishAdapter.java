package com.example.orderfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderfood.CartCountListener;
import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.activity.DishListActivity;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.MerchantDetailActivity;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.MerchantBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final CartCountListener cartCountListener;
    private Fragment fragment = null;
    private Context context;
    private List<Dish> dishList;
    private List<MerchantBean> merchantList;
    private boolean isGridLayout = false;
    private Map<Integer, Integer> dishQuantities = new HashMap<>();
    private DBMysqlHelper dbHelper = DBMysqlHelper.getInstance(context);

    public DishAdapter(Context context, List<Dish> dishList,CartCountListener cartCountListener,
                       Fragment fragment) {
        this.context = context;
        this.dishList = dishList;
        this.cartCountListener = cartCountListener;
        this.fragment = fragment;

    }

    public DishAdapter(DishListActivity context, ArrayList<Dish> dishList, CartCountListener cartCountListener) {
        this.context = context;
        this.dishList = dishList;
        this.cartCountListener = cartCountListener;
    }

    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return isGridLayout ? 1 : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_dish_grid, parent, false);
            return new MerchantViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_dish, parent, false);
            return new DishViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isGridLayout) {
            MerchantBean merchant = merchantList.get(position);
            MerchantViewHolder merchantHolder = (MerchantViewHolder) holder;
            merchantHolder.textViewName.setText(merchant.getName());
            merchantHolder.textViewBusinessHours.setText("营业时间: " + merchant.getBusinessHours());
            merchantHolder.textViewWindowLocation.setText("窗口位置: " + merchant.getWindowLocation());
            merchantHolder.textViewContent.setText("特色美食: " + merchant.getContent());

            // 设置商家图片
            int imageResId = context.getResources().getIdentifier(
                    merchant.getImageName(), "mipmap",context.getPackageName());
            merchantHolder.imageView.setImageResource(imageResId);
            Glide.with(context).load(imageResId).into(merchantHolder.imageView);

            merchantHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApplication.mContext, MerchantDetailActivity.class);
                    intent.putExtra("merchantId", merchant.getMerchantId());
                    context.startActivity(intent);
                }
            });
        } else {
            Dish dish = dishList.get(position);
            DishViewHolder dishHolder = (DishViewHolder) holder;
            dishHolder.textViewName.setText(dish.getName());
            dishHolder.textViewPrice.setText(String.format("%s元/份", dish.getPrice()));
            dishHolder.merchantsName.setVisibility(View.GONE);

           /* // 设置商家图片
            int imageResId = context.getResources().getIdentifier(
                    dish.getImageUrl(), "mipmap",context.getPackageName());
            System.out.println("imageResId: " + imageResId);
            dishHolder.imageView.setImageResource(imageResId);
            Glide.with(context).load(imageResId).into(dishHolder.imageView);
*/
            
            if (dish.getImageUrl().startsWith("content://")) {
                System.out.println("imageResId ggg: " + dish.getImageUrl());
                String imageUrl =dish.getImageUrl();
                Glide.with(context)
                        .load(imageUrl)
                        .into(dishHolder.imageView);
            } else {
                // 设置商家图片
                int imageResId = context.getResources().getIdentifier(
                        dish.getImageUrl(), "mipmap",context.getPackageName());
                System.out.println("imageResId: " + imageResId);
                dishHolder.imageView.setImageResource(imageResId);
                Glide.with(context).load(imageResId).into(dishHolder.imageView);

            }


            dishHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.addToCart(
                            MyApplication.getUserId(),
                            dish.getMerchantId(),
                            dish.getDishId(),
                            1,
                            new DBMysqlHelper.DatabaseCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(context, "添加到购物车成功", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onError(Exception e) {
                                    System.out.println("添加到购物车失败: " + e.getMessage());
                                    Toast.makeText(context, "添加到购物车失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
            });

            dishHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.removeFromCart(
                            MyApplication.getUserId(),
                            dish.getMerchantId(),
                            dish.getDishId(),
                            1,
                            new DBMysqlHelper.DatabaseCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(context, "从 购物车 移除成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(context, "从购物车移除失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
            });

            dishHolder.editTextQuantity.setText(String.valueOf(dishQuantities.getOrDefault(dish.getDishId(), 1)));
            dishHolder.editTextQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {
                        int quantity = Integer.parseInt(s.toString());
                        dishQuantities.put(dish.getDishId(), quantity);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            dishHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return isGridLayout ? merchantList.size() : dishList.size();
    }

    public void updateDishList(List<Dish> newDishList) {
        dishList = newDishList;
        notifyDataSetChanged();
    }

    public void updateMerchantList(List<MerchantBean> newMerchantList) {
        merchantList = newMerchantList;
        notifyDataSetChanged();
    }

    public void setMerchantList(List<MerchantBean> merchantList) {
        this.merchantList = merchantList;
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView merchantsName;
        ImageButton addButton;
        ImageButton removeButton;
        EditText editTextQuantity;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            merchantsName = itemView.findViewById(R.id.merchants);
            addButton = itemView.findViewById(R.id.buttonAdd);
            removeButton = itemView.findViewById(R.id.buttonRemove);
            editTextQuantity = itemView.findViewById(R.id.editTextQuantity);
        }
    }

    static class MerchantViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewBusinessHours;
        TextView textViewWindowLocation;
        TextView textViewContent;

        public MerchantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBusinessHours = itemView.findViewById(R.id.textViewBusinessHours);
            textViewWindowLocation = itemView.findViewById(R.id.textViewWindowLocation);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }



    private void setupCartButtons(Dish dish) {
        // 获取当前商品在购物车中的数量
        dbHelper.getCartItemQuantity(
                MyApplication.getUserId(),
                dish.getMerchantId(),
                dish.getDishId(),
                new DBMysqlHelper.DatabaseCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer quantity) {
                        updateCartUI(quantity);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "获取购物车数据失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateCartUI(Integer quantity) {

    }


}
