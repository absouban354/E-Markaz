package in.tomtontech.markaz.Admin.Fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import in.tomtontech.markaz.R;

public class BlankFragment extends Fragment {

  private WebView webView;
  public BlankFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view= inflater.inflate(R.layout.fragment_admin_blank, container, false);
    webView=(WebView)view.findViewById(R.id.admin_webView);
    return view;
  }

}
