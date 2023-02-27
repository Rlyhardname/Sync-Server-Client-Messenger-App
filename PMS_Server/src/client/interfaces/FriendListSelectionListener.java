package client.interfaces;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import client.ClientOperationGUI;

public class FriendListSelectionListener implements MouseListener {

	ClientOperationGUI gui;

	public FriendListSelectionListener(ClientOperationGUI clientOperationGUI) {
		gui = clientOperationGUI;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel obj = (JLabel) e.getComponent();

		gui.changeLabelColor(gui.getFriendOne(), Color.black);
		gui.changeLabelColor(gui.getFriendTwo(), Color.black);
		gui.changeLabelColor(gui.getFriendThree(), Color.black);
		gui.changeLabelColor(gui.getFriendFour(), Color.black);
		gui.changeLabelColor(gui.getFriendFive(), Color.black);
		gui.changeLabelColor(gui.getFileDemonstrationRoom(), Color.black);

		if (obj.equals(gui.getFriendOne())) {
			String[] friend = gui.getFriendOne().getText().split("\\s+");
			gui.changeLabelColor(gui.getFriendOne(), Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.getFriendTwo())) {
			String[] friend = gui.getFriendTwo().getText().split("\\s+");
			gui.changeLabelColor(gui.getFriendTwo(), Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.getFriendThree())) {
			String[] friend = gui.getFriendThree().getText().split("\\s+");
			gui.changeLabelColor(gui.getFriendThree(), Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.getFriendFour())) {
			String[] friend = gui.getFriendFour().getText().split("\\s+");
			gui.changeLabelColor(gui.getFriendFour(), Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.getFriendFive())) {
			String[] friend = gui.getFriendFive().getText().split("\\s+");
			gui.changeLabelColor(gui.getFriendFive(), Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.getFileDemonstrationRoom())) {
			String[] friend = gui.getFileDemonstrationRoom().getText().split("\\s+");
			gui.changeLabelColor(gui.getFileDemonstrationRoom(), Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
