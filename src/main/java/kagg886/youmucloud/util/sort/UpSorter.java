package kagg886.youmucloud.util.sort;
import java.util.Comparator;

public class UpSorter implements Comparator<SortItem> {
	public static final UpSorter INSTANCE = new UpSorter();
	@Override
	public int compare(SortItem p1, SortItem p2) {
		return p2.value - p1.value;
	}
}
