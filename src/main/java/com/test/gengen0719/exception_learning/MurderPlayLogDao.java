package com.test.gengen0719.exception_learning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

public class MurderPlayLogDao {
	
	/**
	 * ユーザーのプレイ記録をデータベースから読み込み
	 * {@link MurderPlayLog} のListにして返します。
	 *  
	 * @param userName
	 * @return
	 */
	public List<MurderPlayLog> load(String userName)  {
		List<MurderPlayLog> murderPlayLogList = new ArrayList<>();
		try {
			// 例外処理の演習のため、ここではあえて古典的な書き方にしています
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb","root", "root");// 本来はリソースファイルに記載される
			
			PreparedStatement ps = con.prepareStatement("SELECT * FROM MURDER_PLAY_LOG WHERE USER_NAME = ?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				MurderPlayLog murderPlayLog = new MurderPlayLog();
				murderPlayLog.setUserName(userName);
				murderPlayLog.setGameId(rs.getString("GAME_ID"));
				murderPlayLog.setGameName(rs.getString("GAME_NAME"));
				murderPlayLog.setPlayed(rs.getInt("PLAYED"));
				murderPlayLog.setPlayDate(rs.getDate("PLAY_DATE"));
				murderPlayLogList.add(murderPlayLog);
			}
			DbUtils.closeQuietly(con, ps, rs);
		} catch (Exception e) {
			
		} 
		return murderPlayLogList;
	}
	
	
}
