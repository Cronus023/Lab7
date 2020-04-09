package Package7;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class MainFrame extends JFrame {
	private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	
	private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
	private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
	
	private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
	private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
	
	private static final int SMALL_GAP = 5;
	private static final int MEDIUM_GAP = 10;
	private static final int LARGE_GAP = 15;
	
	//server port
	private static final int SERVER_PORT = 4567;
	
	//text fields for name and adres of user
	private final JTextField textFieldFrom;
	private final JTextField textFieldTo;
	
	// text area for output message
	private final JTextArea textAreaIncoming;
	// text area for input message
	private final JTextArea textAreaOutgoing;
	
	public int getServerPort() {
		return SERVER_PORT;
	}
	public JTextArea getTextAreaIncoming() {
		return textAreaIncoming;
	}
	public JTextArea getTextAreaOutgoing() {
		return textAreaOutgoing;
	}
	
	private InstantMessenger messenger;
	public InstantMessenger getMessenger() {
		return messenger;
	}
	public MainFrame(){
		super(FRAME_TITLE);
		setMinimumSize( new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		//window centre
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2,(kit.getScreenSize().height - getHeight()) / 2);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu chatMenu = new JMenu("Меню");
		
		Action logInAction = new AbstractAction("Вход") {
			public void actionPerformed(ActionEvent arg0) {
				String value = JOptionPane.showInputDialog(MainFrame.this, "Введите имя для общения", "Вход", JOptionPane.QUESTION_MESSAGE);
				if (messenger.getDataBase().getUser(value) == null)
					JOptionPane.showMessageDialog(MainFrame.this, "Пользователя с таким именем не существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
				else messenger.setSender(value);
			}
		};
		
		Action findUserAction = new AbstractAction("Поиск пользователя") {
			public void actionPerformed(ActionEvent arg0) {
				String value = JOptionPane.showInputDialog(MainFrame.this, "Введите имя для поиска", "Поиск пользователя", JOptionPane.QUESTION_MESSAGE);
				User user;
				if (messenger.getDataBase().getUser(value) == null) {
					JOptionPane.showMessageDialog(MainFrame.this, "Пользователя "+ value + " не существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					user = messenger.getDataBase().getUser(value);
					JOptionPane.showMessageDialog(MainFrame.this, "Пользователь найден!\n "+ user.getName() + " находится в базе данных.", "Пользователь "+ user.getName(), JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		};
		
		Action openPrivateDialogAction = new AbstractAction("Личное сообщение") {
			public void actionPerformed(ActionEvent arg0) {
				String value = JOptionPane.showInputDialog(MainFrame.this, "Кому: ", "Поиск пользователя", JOptionPane.QUESTION_MESSAGE);
				
				User user;
				if (messenger.getDataBase().getUser(value) == null) {
					JOptionPane.showMessageDialog(MainFrame.this, "Пользователя "+ value + " не существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					user = messenger.getDataBase().getUser(value);
				}
				DialogFrame dialogFrame = new DialogFrame(user, MainFrame.this);
			}
		};
		
		chatMenu.add(logInAction);
		chatMenu.add(findUserAction);
		chatMenu.add(openPrivateDialogAction);
		
		
		menuBar.add(chatMenu);
		textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
		textAreaIncoming.setEditable(false);
		
		// container for scroll output messages
		final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
		
		// fields names
		final JLabel labelFrom = new JLabel("От");
		final JLabel labelTo = new JLabel("Получатель");
		
		textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
		textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);
		textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
		
		// container for scroll input messages
		final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
		
		//panel of input message
		final JPanel messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
		
		// button to send message
		final JButton sendButton = new JButton("Отправить");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(messenger.getSender()!=null)
				    messenger.sendMessage(textFieldTo.getText(),textAreaOutgoing.getText());
				else 
				{
					JOptionPane.showMessageDialog(MainFrame.this, "Войдите в систему!", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} 
		});
		
		// this is draw panel "message"
		final GroupLayout layout2 = new GroupLayout(messagePanel);
		messagePanel.setLayout(layout2);

		layout2.setHorizontalGroup(layout2.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout2.createParallelGroup(Alignment.TRAILING)
				.addGroup(layout2.createSequentialGroup()
				    .addComponent(labelFrom)
				    .addGap(SMALL_GAP)
				    .addComponent(textFieldFrom)
				    .addGap(LARGE_GAP)
				    .addComponent(labelTo)
				    .addGap(SMALL_GAP)
				    .addComponent(textFieldTo))
				.addComponent(scrollPaneOutgoing)
				.addComponent(sendButton))
		    .addContainerGap());
		
		layout2.setVerticalGroup(layout2.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout2.createParallelGroup(Alignment.BASELINE)
						.addComponent(labelFrom)
						.addComponent(textFieldFrom)
						.addComponent(labelTo)
						.addComponent(textFieldTo))
				.addGap(MEDIUM_GAP)
				.addComponent(scrollPaneOutgoing)
				.addGap(MEDIUM_GAP)
				.addComponent(sendButton)
				.addContainerGap());

		// all elements of our window
		final GroupLayout layout1 = new GroupLayout(getContentPane());
		setLayout(layout1);
		layout1.setHorizontalGroup(layout1.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout1.createParallelGroup()
				    .addComponent(scrollPaneIncoming)
				    .addComponent(messagePanel))
				.addContainerGap());
		
		layout1.setVerticalGroup(layout1.createSequentialGroup()
				.addContainerGap()
				.addComponent(scrollPaneIncoming)
				.addGap(MEDIUM_GAP)
				.addComponent(messagePanel)
				.addContainerGap());

		messenger = new InstantMessenger(this);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    final MainFrame frame = new MainFrame();
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    frame.setVisible(true);
			}
		});
	}
}
