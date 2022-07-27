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
import de.xyzerstudios.moneymanager.utils.PortfolioAdapter;
import de.xyzerstudios.moneymanager.utils.Utils;


public class PortfoliosFragment extends Fragment {

    private RecyclerView portfolioRecyclerView;
    private RecyclerView.Adapter portfolioAdapter;
    private SwipeRefreshLayout swipeRefreshPortfolio;

    public PortfoliosFragment() {

    }

    public static PortfoliosFragment newInstance(String param1, String param2) {
        PortfoliosFragment fragment = new PortfoliosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolios, container, false);

        portfolioRecyclerView = view.findViewById(R.id.recyclerViewPortfolios);
        swipeRefreshPortfolio = view.findViewById(R.id.swipeRefreshPortfolios);

        ArrayList<BalancePortfolioItem> portfolioItems = new ArrayList<>();

        portfolioItems.add(new BalancePortfolioItem("Portfolio 1", 31055, 0));
        portfolioItems.add(new BalancePortfolioItem("Portfolio 2", 1195, 1));

        portfolioRecyclerView.setHasFixedSize(true);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        portfolioAdapter = new PortfolioAdapter(getActivity(), getActivity(), portfolioItems, loadPortfolioIdFromSharedPrefs());

        portfolioRecyclerView.setAdapter(portfolioAdapter);

        swipeRefreshPortfolio.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshPortfolio.setRefreshing(false);
            }
        });

        return view;
    }


    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void savePortfolioId(int id) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, id);
        editor.apply();
    }
}