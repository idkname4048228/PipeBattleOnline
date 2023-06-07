import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class GameDemo extends JFrame {
	Game game;
	private JPanel contentPanel; // 視窗的主畫面
	private Shop shop = new Shop();
	private Round round;
	private GameControlPanel controlPanel;

	public void frameStart() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GameDemo frame = new GameDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GameDemo() {
		this.game = new Game();
		this.game.shop = shop;
		this.controlPanel = new GameControlPanel(game, shop);
		this.round = new Round(game, controlPanel);
		this.game.controlPanel = controlPanel;
		this.game.round = round;
		this.controlPanel.round = round;

		setTitle("PipeBattle");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(230, 100, 992, 743);

		contentPanel = new JPanel();
		contentPanel.setBackground(new Color(255, 255, 255));
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPanel);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 576, 400, 0 };
		gbl_contentPane.rowHeights = new int[] { 704, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPane);

		GridBagConstraints gbc_GameMapPanel = new GridBagConstraints();
		gbc_GameMapPanel.fill = GridBagConstraints.BOTH;
		gbc_GameMapPanel.insets = new Insets(0, 0, 0, 0);
		gbc_GameMapPanel.gridx = 0;
		gbc_GameMapPanel.gridy = 0;
		contentPanel.add(game.getPanel(), gbc_GameMapPanel);

		GridBagConstraints gbc_OperatePanel = new GridBagConstraints();
		gbc_OperatePanel.fill = GridBagConstraints.BOTH;
		gbc_OperatePanel.gridx = 1;
		gbc_OperatePanel.gridy = 0;
		contentPanel.add(controlPanel.ControlPanel, gbc_OperatePanel);

	}

	public String getSendStr() {
		System.out.println("demo: sendStr: " + game.getSendStr());
		return game.sendStr;
	}

}
