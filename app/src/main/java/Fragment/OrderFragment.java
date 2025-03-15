package Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ltmb.ltmobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {
    private TextView txtDraft, txtHistory, txtReview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        txtHistory = view.findViewById(R.id.txtHistory);
        txtReview = view.findViewById(R.id.txtReview);
        txtDraft = view.findViewById(R.id.txtDraft);

        // Gọi phương thức để thêm Fragment con
        if (savedInstanceState == null) {
            loadChildFragment(new OrderHistoryFragment());
        }

        txtHistory.setOnClickListener(v -> {
            loadChildFragment(new OrderHistoryFragment());
            setSelected(txtHistory);
        });

        // Xử lý click để chuyển Fragment
//        txtReview.setOnClickListener(v -> {
//            loadChildFragment(new OrderFragment());
//            setSelected(txtReview);
//        });

        txtDraft.setOnClickListener(v -> {
            loadChildFragment(new DraftOrderFragment());
            setSelected(txtDraft);
        });

        return view;
    }
    private void loadChildFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .commit();
    }

    private void setSelected(TextView selectedTextView) {
        txtDraft.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        txtHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        txtReview.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));

        selectedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_main));
    }
}