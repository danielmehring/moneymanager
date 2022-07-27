package de.xyzerstudios.moneymanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.BalanceAdapter;
import de.xyzerstudios.moneymanager.utils.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.charting.CategoryAdapter;

public class BalancesFragment extends Fragment {

    private RecyclerView balanceRecyclerView;
    private RecyclerView.Adapter balanceAdapter;
    private SwipeRefreshLayout swipeRefreshBalance;

    public BalancesFragment() {
    }

    public static BalancesFragment newInstance(String param1, String param2) {
        BalancesFragment fragment = new BalancesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balances, container, false);

        swipeRefreshBalance = view.findViewById(R.id.swipeRefreshBalances);

        ArrayList<BalancePortfolioItem> balanceItems = new ArrayList<>();

        balanceItems.add(new BalancePortfolioItem("Bilanz 1", 31055));
        balanceItems.add(new BalancePortfolioItem("Bilanz 2", -1196));

        balanceRecyclerView = view.findViewById(R.id.recyclerViewBalances);
        balanceRecyclerView.setHasFixedSize(true);
        balanceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        balanceAdapter = new BalanceAdapter(getActivity(), balanceItems);

        balanceRecyclerView.setAdapter(balanceAdapter);


        swipeRefreshBalance.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshBalance.setRefreshing(false);
            }
        });

        return view;
    }

}