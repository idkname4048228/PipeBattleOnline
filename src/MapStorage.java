import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapStorage {
	ArrayList<ArrayList<String>> mapFile = new ArrayList<>();

	MapStorage() {
		// 讀地圖檔，並把他們都放到 mapStorage 裡面
		File f = new File("src/maps/map.txt");
		Scanner sc = null;
		ArrayList<String> strArray = new ArrayList<String>();
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (sc.hasNext()) {
			String tmp = sc.nextLine();
			if (tmp.length() != 0)
				strArray.add(tmp);
			else
				break;
		}

		ArrayList<ArrayList<String>> unitArray = new ArrayList<>();
		for (int i = 0; i < strArray.size(); i++) {
			ArrayList<String> rowArray = new ArrayList<String>();
			for (String unit : strArray.get(i).split(" ")) {
				rowArray.add(unit);
			}
			unitArray.add(rowArray);
		}
		mapFile = unitArray;
	}

	public ArrayList<ArrayList<String>> getMapFile() {
		return mapFile;
	}
}
