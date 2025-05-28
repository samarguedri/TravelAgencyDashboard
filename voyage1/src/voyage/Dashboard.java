package voyage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Dashboard extends JFrame {
    
    private Destination currentDestination;
    private List<PackageVoyage> packages;
    private JPanel sidebar, contentPanel;
    private List<Destination> destinations;
    private List<Hotel> hotels;
    private List<Restaurant> restaurants;
    private JTextArea descriptionArea;
    private static final Color TEAL_COLOR = new Color(0, 128, 128);
    private static final Color HOVER_COLOR = new Color(0, 150, 150);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font DESC_FONT = new Font("Arial", Font.PLAIN, 14);
    private Connection conn;
    private JTextField nomField;


    public Dashboard() {
        
        setTitle("Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

       
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root", "samar123");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initializeData();
        createUI();

        setVisible(true);
    }

    private void createUI() {
        sidebar = createSidebar();
        contentPanel = createContentPanel();

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(250, getHeight()));
        panel.setBackground(TEAL_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addLogo(panel);
        addMenuButtons(panel);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addLogo(JPanel panel) {
        ImageIcon logoIcon = loadScaledImage("C:/Users/samar/Desktop/3.png", 190, 180);
        if (logoIcon != null) {
            JLabel logo = new JLabel(logoIcon);
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            logo.setBorder(BorderFactory.createEmptyBorder(-35, 0, 0, 0));
            panel.add(logo);
        }
    }

    private void addMenuButtons(JPanel panel) {
        String[] menuItems = {"Accueil", "View Hotels", "Destination", "Restaurant", "Packages", "Admin"};
        String[] iconPaths = {
            "C:/Users/samar/Desktop/4.png",
            "C:/Users/samar/Desktop/signe-de-lhotel.png",
            "C:/Users/samar/Desktop/mondial.png",
            "C:/Users/samar/Desktop/le-restaurant.png",
            "C:/Users/samar/Desktop/emballer.png",
            "C:/Users/samar/Desktop/parametre-utilisateur.png",
        };

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(TEAL_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        for (int i = 0; i < menuItems.length; i++) {
            JButton btn = createSidebarButton(menuItems[i], iconPaths[i]);
            String item = menuItems[i];

            btn.addActionListener(e -> handleMenuAction(item));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            buttonPanel.add(btn);

            if (i < menuItems.length - 1) {
                buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        panel.add(buttonPanel);
    }

  private void handleMenuAction(String item) {
    switch (item) {
        case "View Hotels":
            showHotels();
            break;
        case "Destination":
            showDestinations();
            break;
        case "Restaurant":
            showRestaurants();
            break;
        case "Accueil":
            showHome();
            break;
        case "Admin":
            showAdminLogin();
            break;
        case "Packages":
            showPackages();
            break;
        default:
            System.err.println("Action non définie pour : " + item);
    }
}
    private JButton createSidebarButton(String text, String iconPath) {
        ImageIcon icon = null;

        try {
            icon = new ImageIcon(iconPath);
            Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'icône : " + iconPath);
        }

        JButton button = new JButton(text, icon);
        button.setPreferredSize(new Dimension(220, 50));
        button.setMaximumSize(new Dimension(220, 50));
        button.setMinimumSize(new Dimension(220, 50));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 128, 128));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(15);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 150, 150));
                button.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Color.WHITE));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 128, 128));
                button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }
        });

        return button;
    }

    private ImageIcon loadScaledImage(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getImage() != null) {
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image : " + path);
        }
        return null;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            private Image background = loadImage("C:/Users/samar/Desktop/2.jpg");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        addHeader(panel);
        return panel;
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(path).getImage();
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image de fond : " + path);
            return null;
        }
    }

    private void addHeader(JPanel panel) {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(TEAL_COLOR.getRed(), TEAL_COLOR.getGreen(), TEAL_COLOR.getBlue(), 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        headerPanel.setPreferredSize(new Dimension(panel.getWidth(), 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 25));

        JLabel title = new JLabel("Découvrez des destinations inédites et planifiez votre voyage");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);
        headerPanel.add(title);

        panel.add(headerPanel, BorderLayout.NORTH);
    }

    private void showHome() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel homePanel = new JPanel(new BorderLayout()) {
            private Image background = loadImage("C:/Users/samar/Desktop/2.jpg");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        addHeader(homePanel);
        contentPanel.add(homePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDestinations() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(230, 240, 250);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        cardsPanel.setOpaque(false);

        for (Destination dest : destinations) {
            cardsPanel.add(createDestinationCard(dest));
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel descriptionPanel = createDescriptionPanel();
        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(descriptionPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHotels() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(230, 240, 250);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        cardsPanel.setOpaque(false);

        for (Hotel hotel : hotels) {
            cardsPanel.add(createHotelCard(hotel));
        }

        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel descriptionPanel = createDescriptionPanel();
        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(descriptionPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showRestaurants() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(230, 240, 250);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        cardsPanel.setOpaque(false);

        for (Restaurant restaurant : restaurants) {
            cardsPanel.add(createRestaurantCard(restaurant));
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel descriptionPanel = createDescriptionPanel();
        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(descriptionPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Description détaillée");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(DESC_FONT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDestinationCard(Destination dest) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            private Image background;

            {
                try {
                    background = new ImageIcon(dest.getImagePath()).getImage();
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image : " + dest.getImagePath());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 120));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        card.setPreferredSize(new Dimension(300, 350));

        addDestinationInfo(card, dest);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                showHotelsAndRestaurantsForDestination(dest);
            }

            public void mouseEntered(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(TEAL_COLOR, 2)
                ));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
                ));
            }
        });

        return card;
    }

    private void showHotelsAndRestaurantsForDestination(Destination dest) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(230, 240, 250);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet Hôtels
        JPanel hotelsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        hotelsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        hotelsPanel.setOpaque(false);

        for (Hotel hotel : dest.getHotels()) {
            JPanel card = createHotelCard(hotel);
            JButton reserverButton = (JButton) ((JPanel) card.getComponent(1)).getComponent(0);
            reserverButton.addActionListener(e -> {
                showReservationForm("Hôtel", hotel.getNom(), hotel.getPrix());
                currentDestination = dest;
            });
            hotelsPanel.add(card);
        }

        JScrollPane hotelsScroll = new JScrollPane(hotelsPanel);
        hotelsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        hotelsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        hotelsScroll.setBorder(BorderFactory.createEmptyBorder());
        hotelsScroll.getViewport().setOpaque(false);
        hotelsScroll.setOpaque(false);

        tabbedPane.addTab("Hôtels (" + dest.getHotels().size() + ")", hotelsScroll);

        // Onglet Restaurants
        JPanel restaurantsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        restaurantsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        restaurantsPanel.setOpaque(false);

        for (Restaurant restaurant : dest.getRestaurants()) {
            JPanel card = createRestaurantCard(restaurant);
            JButton reserverButton = (JButton) ((JPanel) card.getComponent(1)).getComponent(0);
            reserverButton.addActionListener(e -> {
                showReservationForm("Restaurant", restaurant.getNom(), restaurant.getPrixMoyen());
                currentDestination = dest;
            });
            restaurantsPanel.add(card);
        }

        JScrollPane restaurantsScroll = new JScrollPane(restaurantsPanel);
        restaurantsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        restaurantsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        restaurantsScroll.setBorder(BorderFactory.createEmptyBorder());
        restaurantsScroll.getViewport().setOpaque(false);
        restaurantsScroll.setOpaque(false);

        tabbedPane.addTab("Restaurants (" + dest.getRestaurants().size() + ")", restaurantsScroll);

        JPanel descriptionPanel = createDescriptionPanel();
        descriptionArea.setText(dest.getDescription());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(descriptionPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createHotelCard(Hotel hotel) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            private Image background;

            {
                try {
                    background = new ImageIcon(hotel.getImagePath()).getImage();
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image : " + hotel.getImagePath());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 120));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        card.setPreferredSize(new Dimension(300, 350));

        addHotelInfo(card, hotel);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                descriptionArea.setText(hotel.getDescription());
            }

            public void mouseEntered(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(TEAL_COLOR, 2)
                ));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
                ));
            }
        });

        JButton reserverButton = new JButton("Réserver");
        reserverButton.setBackground(TEAL_COLOR);
        reserverButton.setForeground(Color.WHITE);
        reserverButton.addActionListener(e -> {
            showReservationForm("Hôtel", hotel.getNom(), hotel.getPrix());
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(reserverButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRestaurantCard(Restaurant restaurant) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            private Image background;

            {
                try {
                    background = new ImageIcon(restaurant.getImagePath()).getImage();
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image : " + restaurant.getImagePath());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 120));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        card.setPreferredSize(new Dimension(300, 350));

        addRestaurantInfo(card, restaurant);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                descriptionArea.setText(restaurant.getDescription());
            }

            public void mouseEntered(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(TEAL_COLOR, 2)
                ));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
                ));
            }
        });

        JButton reserverButton = new JButton("Réserver");
        reserverButton.setBackground(TEAL_COLOR);
        reserverButton.setForeground(Color.WHITE);
        reserverButton.addActionListener(e -> {
            showReservationForm("Restaurant", restaurant.getNom(), restaurant.getPrixMoyen());
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(reserverButton);
        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private void addDestinationInfo(JPanel card, Destination dest) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        JLabel nameLabel = new JLabel("<html><h2 style='color:white;margin-bottom:5px;'>" + dest.getNom() + "</h2></html>");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("<html><b style='color:#cccccc;'>Date : </b><span style='color:white;'>" + dest.getDate() + "</span></html>");
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><i style='color:#eeeeee;'>" + dest.getDescriptionCourte() + "</i></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(dateLabel);
        infoPanel.add(descLabel);

        card.add(infoPanel, BorderLayout.CENTER);
    }

    private void addHotelInfo(JPanel card, Hotel hotel) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        JLabel nameLabel = new JLabel("<html><h2 style='color:white;margin-bottom:5px;'>" + hotel.getNom() + "</h2></html>");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel starsLabel = new JLabel("<html><b style='color:#ffd700;'>" + getStarRating(hotel.getStars()) + "</b></html>");
        starsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("<html><b style='color:#7fffd4;'>Prix : " + String.format("%.2f", hotel.getPrix()) + " €/nuit</b></html>");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel descLabel = new JLabel("<html><i style='color:#eeeeee;'>" + hotel.getDescriptionCourte() + "</i></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(starsLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(descLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        infoPanel.add(Box.createVerticalStrut(10));
        card.add(infoPanel, BorderLayout.CENTER);
    }

    private void addRestaurantInfo(JPanel card, Restaurant restaurant) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        JLabel nameLabel = new JLabel("<html><h2 style='color:white;margin-bottom:5px;'>" + restaurant.getNom() + "</h2></html>");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cuisineLabel = new JLabel("<html><b style='color:#cccccc;'>Cuisine : </b><span style='color:white;'>" + restaurant.getCuisine() + "</span></html>");
        cuisineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("<html><b style='color:#7fffd4;'>Prix moyen : " + String.format("%.2f", restaurant.getPrixMoyen()) + " €</b></html>");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel descLabel = new JLabel("<html><i style='color:#eeeeee;'>" + restaurant.getDescriptionCourte() + "</i></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(cuisineLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(descLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        infoPanel.add(Box.createVerticalStrut(10));
        card.add(infoPanel, BorderLayout.CENTER);
    }

    private String getStarRating(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            sb.append("★");
        }
        for (int i = stars; i < 5; i++) {
            sb.append("☆");
        }
        return sb.toString();
    }

    private JPanel createPackageCard(PackageVoyage pack) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            private Image background;

            {
                try {
                    background = new ImageIcon(pack.getImagePath()).getImage();
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image : " + pack.getImagePath());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 120));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        card.setPreferredSize(new Dimension(300, 350));

        addPackageInfo(card, pack);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                descriptionArea.setText(pack.getDescription());
            }

            public void mouseEntered(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(TEAL_COLOR, 2)
                ));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
                ));
            }
        });

        JButton reserverButton = new JButton("Réserver");
        reserverButton.setBackground(TEAL_COLOR);
        reserverButton.setForeground(Color.WHITE);
        reserverButton.addActionListener(e -> {
            showReservationForm("Package", pack.getNom(), pack.getPrix());
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(reserverButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addPackageInfo(JPanel card, PackageVoyage pack) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        JLabel nameLabel = new JLabel("<html><h2 style='color:white;margin-bottom:5px;'>" + pack.getNom() + "</h2></html>");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel destLabel = new JLabel("<html><b style='color:#cccccc;'>Destination : </b><span style='color:white;'>" + pack.getDestination().getNom() + "</span></html>");
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("<html><b style='color:#7fffd4;'>Prix : " + String.format("%.2f", pack.getPrix()) + " €</b></html>");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel descLabel = new JLabel("<html><i style='color:#eeeeee;'>" + pack.getDescriptionCourte() + "</i></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(destLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(descLabel);

        card.add(infoPanel, BorderLayout.CENTER);
    }

    private void showPackages() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(230, 240, 250);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        cardsPanel.setOpaque(false);

        for (PackageVoyage pack : packages) {
            cardsPanel.add(createPackageCard(pack));
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel descriptionPanel = createDescriptionPanel();
        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(descriptionPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void initializeData() {
        destinations = new ArrayList<>();
        hotels = new ArrayList<>();
        restaurants = new ArrayList<>();
        packages = new ArrayList<>();

        try {
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Destination");
            while (rs.next()) {
                Destination dest = new Destination(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("date"),
                    rs.getDouble("prix"),
                    rs.getString("descriptionCourte"),
                    rs.getString("description"),
                    rs.getBoolean("disponible"),
                    rs.getString("imagePath")
                );
                destinations.add(dest);
            }
            rs.close();
            stmt.close();

            
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Hotel");
            while (rs.next()) {
                Hotel hotel = new Hotel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("stars"),
                    rs.getDouble("prix"),
                    rs.getString("descriptionCourte"),
                    rs.getString("description"),
                    rs.getString("imagePath")
                );
                hotels.add(hotel);
                int destId = rs.getInt("destination_id");
                for (Destination dest : destinations) {
                    if (dest.getId() == destId) {
                        dest.addHotel(hotel);
                        break;
                    }
                }
            }
            rs.close();
            stmt.close();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Restaurant");
            while (rs.next()) {
                Restaurant restaurant = new Restaurant(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("cuisine"),
                    rs.getDouble("prixMoyen"),
                    rs.getString("descriptionCourte"),
                    rs.getString("description"),
                    rs.getString("imagePath")
                );
                restaurants.add(restaurant);
                int destId = rs.getInt("destination_id");
                for (Destination dest : destinations) {
                    if (dest.getId() == destId) {
                        dest.addRestaurant(restaurant);
                        break;
                    }
                }
            }
            rs.close();
            stmt.close();

            // Fetch Packages
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT p.*, d.nom AS dest_nom, d.date, d.prix AS dest_prix, d.descriptionCourte AS dest_descCourte, " +
                                   "d.description AS dest_description, d.disponible, d.imagePath AS dest_imagePath, " +
                                   "h.nom AS hotel_nom, h.stars, h.prix AS hotel_prix, h.descriptionCourte AS hotel_descCourte, " +
                                   "h.description AS hotel_description, h.imagePath AS hotel_imagePath, " +
                                   "r.nom AS rest_nom, r.cuisine, r.prixMoyen, r.descriptionCourte AS rest_descCourte, " +
                                   "r.description AS rest_description, r.imagePath AS rest_imagePath " +
                                   "FROM PackageVoyage p " +
                                   "LEFT JOIN Destination d ON p.destination_id = d.id " +
                                   "LEFT JOIN Hotel h ON p.hotel_id = h.id " +
                                   "LEFT JOIN Restaurant r ON p.restaurant_id = r.id");
            while (rs.next()) {
                Destination dest = new Destination(
                    rs.getInt("destination_id"),
                    rs.getString("dest_nom"),
                    rs.getString("date"),
                    rs.getDouble("dest_prix"),
                    rs.getString("dest_descCourte"),
                    rs.getString("dest_description"),
                    rs.getBoolean("disponible"),
                    rs.getString("dest_imagePath")
                );
                Hotel hotel = new Hotel(
                    rs.getInt("hotel_id"),
                    rs.getString("hotel_nom"),
                    rs.getInt("stars"),
                    rs.getDouble("hotel_prix"),
                    rs.getString("hotel_descCourte"),
                    rs.getString("hotel_description"),
                    rs.getString("hotel_imagePath")
                );
                Restaurant restaurant = new Restaurant(
                    rs.getInt("restaurant_id"),
                    rs.getString("rest_nom"),
                    rs.getString("cuisine"),
                    rs.getDouble("prixMoyen"),
                    rs.getString("rest_descCourte"),
                    rs.getString("rest_description"),
                    rs.getString("rest_imagePath")
                );
                PackageVoyage pack = new PackageVoyage(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getString("descriptionCourte"),
                    rs.getString("description"),
                    rs.getString("imagePath"),
                    dest,
                    hotel,
                    restaurant
                );
                packages.add(pack);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReservationForm(String type, String name, double price) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Réservation - " + type + ": " + name);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel priceLabel = new JLabel("Prix: " + String.format("%.2f", price) + " €");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(priceLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx++;
        JTextField nomField = new JTextField(20);
        formPanel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx++;
        JTextField prenomField = new JTextField(20);
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx++;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton payerButton = new JButton("Payer");
        payerButton.setBackground(TEAL_COLOR);
        payerButton.setForeground(Color.WHITE);
        payerButton.addActionListener(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Check if user exists
                PreparedStatement pstmt = conn.prepareStatement("SELECT id, balance FROM user WHERE email = ?");
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                int userId;
                double balance;
                if (rs.next()) {
                    userId = rs.getInt("id");
                    balance = rs.getDouble("balance");
                    rs.close();
                    pstmt.close();
                    
                    // Check if balance is sufficient
                    if (balance >= price) {
                        // Deduct the amount from user's balance
                        PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE user SET balance = balance - ? WHERE id = ?"
                        );
                        updateStmt.setDouble(1, price);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                        updateStmt.close();
                        
                        // Proceed with reservation
                        saveReservation(type, name, price,userId);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Paiement effectué avec succès! Nouveau solde: " + String.format("%.2f", (balance - price)) + " €",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Return to appropriate view
                        if (type.equals("Package")) {
                            showPackages();
                        } else if (currentDestination != null) {
                            showHotelsAndRestaurantsForDestination(currentDestination);
                        } else if (type.equals("Hôtel")) {
                            showHotels();
                        } else {
                            showRestaurants();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Solde insuffisant. Votre solde actuel est de " + String.format("%.2f", balance) + " €",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    rs.close();
                    pstmt.close();
                    JOptionPane.showMessageDialog(this,
    "Utilisateur non trouvé. Veuillez vous inscrire via un autre processus.",
    "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la vérification de l'utilisateur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(e -> {
            if (type.equals("Package")) {
                showPackages();
            } else if (currentDestination != null) {
                showHotelsAndRestaurantsForDestination(currentDestination);
            } else if (type.equals("Hôtel")) {
                showHotels();
            } else {
                showRestaurants();
            }
        });

        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> {
            if (type.equals("Package")) {
                showPackages();
            } else if (currentDestination != null) {
                showHotelsAndRestaurantsForDestination(currentDestination);
            } else if (type.equals("Hôtel")) {
                showHotels();
            } else {
                showRestaurants();
            }
        });

        buttonPanel.add(payerButton);
        buttonPanel.add(annulerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

 

   private void saveReservation(String type, String name, double price, int userId) {
    try (PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO reservation (type, nom_item, prix, userID) VALUES (?, ?, ?, ?)"
    )) {
        pstmt.setString(1, type);
        pstmt.setString(2, name);
        pstmt.setDouble(3, price);
        pstmt.setInt(4, userId);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Réservation enregistrée avec succès pour l'utilisateur ID: " + userId);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Aucune réservation n'a été enregistrée. Vérifiez les données.", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Erreur lors de l'enregistrement de la réservation: " + ex.getMessage(), 
            "Erreur", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); // Log the error for debugging
    }
}

    @Override
    public void dispose() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
        super.dispose();
    }
   private boolean isAdminLoggedIn = false;

private void showAdminLogin() {
    if (isAdminLoggedIn) {
        showAdminDashboard();
        return;
    }

    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    JPanel loginPanel = new JPanel(new GridBagLayout());
    loginPanel.setBackground(Color.WHITE);
    loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 5);

    JLabel titleLabel = new JLabel("Connexion Administrateur");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    gbc.gridwidth = 2;
    loginPanel.add(titleLabel, gbc);

    gbc.gridwidth = 1;
    gbc.gridy++;
    loginPanel.add(new JLabel("Nom d'utilisateur:"), gbc);
    gbc.gridx++;
    JTextField usernameField = new JTextField(20);
    loginPanel.add(usernameField, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    loginPanel.add(new JLabel("Mot de passe:"), gbc);
    gbc.gridx++;
    JPasswordField passwordField = new JPasswordField(20);
    loginPanel.add(passwordField, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    buttonPanel.setBackground(Color.WHITE);

    JButton loginButton = new JButton("Connexion");
    loginButton.setBackground(TEAL_COLOR);
    loginButton.setForeground(Color.WHITE);
   loginButton.addActionListener(e -> {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.equals("admin") && password.equals("admin")) {
            isAdminLoggedIn = true;
            showAdminDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });

    JButton cancelButton = new JButton("Annuler");
    cancelButton.addActionListener(e -> showHome());

    buttonPanel.add(loginButton);
    buttonPanel.add(cancelButton);

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    loginPanel.add(buttonPanel, gbc);

    contentPanel.add(loginPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
    
}

private void showAdminDashboard() {
    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    JPanel adminPanel = new JPanel(new BorderLayout());
    adminPanel.setBackground(Color.WHITE);

    // Création des boutons admin
    JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
    buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    buttonsPanel.setBackground(Color.WHITE);

    
    String[] adminButtons = {"Gérer les clients", "Visualisation", "Ajouter Destination"};
    String[] adminIcons = {
        "C:/Users/samar/Desktop/client.png",
        "C:/Users/samar/Desktop/visualisation.png",
        "C:/Users/samar/Desktop/add_destination.png"
    };

    for (int i = 0; i < adminButtons.length; i++) {
        JButton btn = createAdminButton(adminButtons[i], adminIcons[i]);
        String action = adminButtons[i];
        btn.addActionListener(e -> handleAdminAction(action));
        buttonsPanel.add(btn);
    }

   
    JButton logoutButton = new JButton("Déconnexion");
    logoutButton.setBackground(Color.RED);
    logoutButton.setForeground(Color.WHITE);
   logoutButton.addActionListener(e -> {
    isAdminLoggedIn = false;
    showHome();
    showAdminLogin();
});

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(logoutButton);
    bottomPanel.setBackground(Color.WHITE);

    adminPanel.add(buttonsPanel, BorderLayout.CENTER);
    adminPanel.add(bottomPanel, BorderLayout.SOUTH);
    
    contentPanel.add(adminPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}

private JButton createAdminButton(String text, String iconPath) {
    ImageIcon icon = loadScaledImage(iconPath, 40, 40);
    JButton button = new JButton(text, icon);
    button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    button.setForeground(Color.WHITE);
    button.setBackground(TEAL_COLOR);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    button.setHorizontalTextPosition(SwingConstants.CENTER);
    button.setVerticalTextPosition(SwingConstants.BOTTOM);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    button.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            button.setBackground(HOVER_COLOR);
        }

        public void mouseExited(MouseEvent e) {
            button.setBackground(TEAL_COLOR);
        }
    });

    return button;
}

private void handleAdminAction(String action) {
   
    JPanel previousPanel = (JPanel) contentPanel.getComponent(0);
    
    switch (action) {
        case "Gérer les clients":
            showClientManagement();
            break;
        case "Visualisation":
            showVisualization();
            break;
        case "Ajouter Destination":
            showDestinationManagement();
            break;
        default:
            JOptionPane.showMessageDialog(this, "Fonctionnalité non implémentée", "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
private void showClientManagement() {
    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    JPanel clientPanel = new JPanel(new BorderLayout());
    clientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton addButton = new JButton("Ajouter Client");
    JButton editButton = new JButton("Modifier Client");
    JButton deleteButton = new JButton("Supprimer Client");
    
    addButton.setBackground(TEAL_COLOR);
    addButton.setForeground(Color.WHITE);
    editButton.setBackground(TEAL_COLOR);
    editButton.setForeground(Color.WHITE);
    deleteButton.setBackground(TEAL_COLOR);
    deleteButton.setForeground(Color.WHITE);
    
    String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Montant"};
    Object[][] data = fetchClientData();
    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };
    
    JTable clientTable = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(clientTable);

    
    JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createTitledBorder("Informations client"));
    
    JTextField nomField = new JTextField();
    JTextField prenomField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField montantField = new JTextField();
    
    formPanel.add(new JLabel("Nom:"));
    formPanel.add(nomField);
    formPanel.add(new JLabel("Prénom:"));
    formPanel.add(prenomField);
    formPanel.add(new JLabel("Email:"));
    formPanel.add(emailField);
    formPanel.add(new JLabel("Montant:"));
    formPanel.add(montantField);

  
    addButton.addActionListener(e -> {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String montantStr = montantField.getText().trim();
        
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || montantStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double montant = Double.parseDouble(montantStr);
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO User (nom, prenom, email, balance) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setDouble(4, montant);
            pstmt.executeUpdate();
            
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                model.addRow(new Object[]{id, nom, prenom, email, montant});
            }
            
            
            nomField.setText("");
            prenomField.setText("");
            emailField.setText("");
            montantField.setText("");
            
            JOptionPane.showMessageDialog(this, "Client ajouté avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });

   
    editButton.addActionListener(e -> {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String montantStr = montantField.getText().trim();
        
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || montantStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double montant = Double.parseDouble(montantStr);
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE User SET nom = ?, prenom = ?, email = ?, balance = ? WHERE id = ?"
            );
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setDouble(4, montant);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            
            // Mettre à jour le tableau
            model.setValueAt(nom, selectedRow, 1);
            model.setValueAt(prenom, selectedRow, 2);
            model.setValueAt(email, selectedRow, 3);
            model.setValueAt(montant, selectedRow, 4);
            
            JOptionPane.showMessageDialog(this, "Client modifié avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });

    // Action pour supprimer un client
    deleteButton.addActionListener(e -> {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Êtes-vous sûr de vouloir supprimer ce client?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM User WHERE id = ?");
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                
                
                model.removeRow(selectedRow);
                
                JOptionPane.showMessageDialog(this, "Client supprimé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

 
    clientTable.getSelectionModel().addListSelectionListener(e -> {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0) {
            nomField.setText((String) model.getValueAt(selectedRow, 1));
            prenomField.setText((String) model.getValueAt(selectedRow, 2));
            emailField.setText((String) model.getValueAt(selectedRow, 3));
            montantField.setText(model.getValueAt(selectedRow, 4).toString());
        }
    });

    toolPanel.add(addButton);
    toolPanel.add(editButton);
    toolPanel.add(deleteButton);

    clientPanel.add(toolPanel, BorderLayout.NORTH);
    clientPanel.add(scrollPane, BorderLayout.CENTER);
    clientPanel.add(formPanel, BorderLayout.SOUTH);

    contentPanel.add(clientPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}

private Object[][] fetchClientData() {
   
    try {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, nom, prenom, email, balance FROM User");
        
        List<Object[]> rows = new ArrayList<>();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getDouble("balance")
            };
            rows.add(row);
        }
        
        return rows.toArray(new Object[0][]);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des clients: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        return new Object[0][0];
    }
}


    private void showDestinationManagement() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Onglet Destinations
        JPanel destinationPanel = createManagementPanel("destination");
        tabbedPane.addTab("Destinations", destinationPanel);
        
        // Onglet Hôtels
        JPanel hotelPanel = createManagementPanel("hotel");
        tabbedPane.addTab("Hôtels", hotelPanel);
        
        // Onglet Restaurants
        JPanel restaurantPanel = createManagementPanel("restaurant");
        tabbedPane.addTab("Restaurants", restaurantPanel);
        
        // Onglet Packages
        JPanel packagePanel = createManagementPanel("packageVoyage");
        tabbedPane.addTab("Packages", packagePanel);

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createManagementPanel(String entityType) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Barre d'outils
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        
        addButton.setBackground(TEAL_COLOR);
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(TEAL_COLOR);
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(TEAL_COLOR);
        deleteButton.setForeground(Color.WHITE);
        
        // Tableau
        String[] columnNames = getColumnNamesForEntity(entityType);
        Object[][] data = fetchEntityData(entityType);
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Form panel
        JPanel formPanel = createFormPanelForEntity(entityType);

        // Actions des boutons
        addButton.addActionListener(e -> showAddEntityDialog(entityType, null, model));
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                showAddEntityDialog(entityType, getEntityDataFromRow(table, selectedRow, entityType), model);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un élément", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                deleteEntity(entityType, table, selectedRow, model);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un élément", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        toolPanel.add(addButton);
        toolPanel.add(editButton);
        toolPanel.add(deleteButton);

        panel.add(toolPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private String[] getColumnNamesForEntity(String entityType) {
        switch (entityType) {
            case "destination":
                return new String[]{"ID", "Nom", "Date", "Prix", "Disponible"};
            case "hotel":
                return new String[]{"ID", "Nom", "Étoiles", "Prix", "Destination"};
            case "restaurant":
                return new String[]{"ID", "Nom", "Cuisine", "Prix moyen", "Destination"};
            case "packageVoyage":
                return new String[]{"ID", "Nom", "Prix", "Destination", "Hôtel", "Restaurant"};
            default:
                return new String[]{};
        }
    }

    private Object[][] fetchEntityData(String entityType) {
        try {
            String query = "";
            switch (entityType) {
                case "destination":
                    query = "SELECT * FROM Destination";
                    break;
                case "hotel":
                    query = "SELECT h.*, d.nom AS destination_nom FROM Hotel h LEFT JOIN Destination d ON h.destination_id = d.id";
                    break;
                case "restaurant":
                    query = "SELECT r.*, d.nom AS destination_nom FROM Restaurant r LEFT JOIN Destination d ON r.destination_id = d.id";
                    break;
                case "packageVoyage":
                    query = "SELECT p.*, d.nom AS destination_nom, h.nom AS hotel_nom, r.nom AS restaurant_nom " +
                            "FROM PackageVoyage p " +
                            "LEFT JOIN Destination d ON p.destination_id = d.id " +
                            "LEFT JOIN Hotel h ON p.hotel_id = h.id " +
                            "LEFT JOIN Restaurant r ON p.restaurant_id = r.id";
                    break;
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            List<Object[]> rows = new ArrayList<>();
            while (rs.next()) {
                Object[] row = null;
                switch (entityType) {
                    case "destination":
                        row = new Object[]{
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("date"),
                            rs.getDouble("prix"),
                            rs.getBoolean("disponible")
                        };
                        break;
                    case "hotel":
                        row = new Object[]{
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("stars"),
                            rs.getDouble("prix"),
                            rs.getString("destination_nom")
                        };
                        break;
                    case "restaurant":
                        row = new Object[]{
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("cuisine"),
                            rs.getDouble("prixMoyen"),
                            rs.getString("destination_nom")
                        };
                        break;
                    case "packageVoyage":
                        row = new Object[]{
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getDouble("prix"),
                            rs.getString("destination_nom"),
                            rs.getString("hotel_nom"),
                            rs.getString("restaurant_nom")
                        };
                        break;
                }
                if (row != null) {
                    rows.add(row);
                }
            }
            
            rs.close();
            stmt.close();
            return rows.toArray(new Object[0][]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return new Object[0][0];
        }
    }

    private Object[] getEntityDataFromRow(JTable table, int row, String entityType) {
        int colCount = table.getColumnCount();
        Object[] data = new Object[colCount];
        for (int i = 0; i < colCount; i++) {
            data[i] = table.getValueAt(row, i);
        }
        return data;
    }

    private JPanel createFormPanelForEntity(String entityType) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Détails " + entityType));
        
        switch (entityType) {
            case "destination":
                panel.add(new JLabel("Nom:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Date:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Prix:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description courte:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Disponible:"));
                panel.add(new JCheckBox());
                panel.add(new JLabel("Image:"));
                JTextField imageField = new JTextField(10);
                JButton browseButton = new JButton("Parcourir");
                browseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
                    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        imageField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel imagePanel = new JPanel(new BorderLayout());
                imagePanel.add(imageField, BorderLayout.CENTER);
                imagePanel.add(browseButton, BorderLayout.EAST);
                panel.add(imagePanel);
                break;
            case "hotel":
                panel.add(new JLabel("Nom:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Étoiles:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Prix:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description courte:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Destination:"));
                JComboBox<String> destCombo = new JComboBox<>();
                loadDestinations(destCombo);
                panel.add(destCombo);
                panel.add(new JLabel("Image:"));
                JTextField hotelImageField = new JTextField(10);
                JButton hotelBrowseButton = new JButton("Parcourir");
                hotelBrowseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
                    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        hotelImageField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel hotelImagePanel = new JPanel(new BorderLayout());
                hotelImagePanel.add(hotelImageField, BorderLayout.CENTER);
                hotelImagePanel.add(hotelBrowseButton, BorderLayout.EAST);
                panel.add(hotelImagePanel);
                break;
            case "restaurant":
                panel.add(new JLabel("Nom:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Cuisine:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Prix moyen:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description courte:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Destination:"));
                JComboBox<String> restDestCombo = new JComboBox<>();
                loadDestinations(restDestCombo);
                panel.add(restDestCombo);
                panel.add(new JLabel("Image:"));
                JTextField restImageField = new JTextField(10);
                JButton restBrowseButton = new JButton("Parcourir");
                restBrowseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
                    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        restImageField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel restImagePanel = new JPanel(new BorderLayout());
                restImagePanel.add(restImageField, BorderLayout.CENTER);
                restImagePanel.add(restBrowseButton, BorderLayout.EAST);
                panel.add(restImagePanel);
                break;
            case "packageVoyage":
                panel.add(new JLabel("Nom:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Prix:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description courte:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Description:"));
                panel.add(new JTextField(15));
                panel.add(new JLabel("Destination:"));
                JComboBox<String> packDestCombo = new JComboBox<>();
                loadDestinations(packDestCombo);
                panel.add(packDestCombo);
                panel.add(new JLabel("Hôtel:"));
                JComboBox<String> hotelCombo = new JComboBox<>();
                loadHotels(hotelCombo);
                panel.add(hotelCombo);
                panel.add(new JLabel("Restaurant:"));
                JComboBox<String> restCombo = new JComboBox<>();
                loadRestaurants(restCombo);
                panel.add(restCombo);
                panel.add(new JLabel("Image:"));
                JTextField packImageField = new JTextField(10);
                JButton packBrowseButton = new JButton("Parcourir");
                packBrowseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
                    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        packImageField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel packImagePanel = new JPanel(new BorderLayout());
                packImagePanel.add(packImageField, BorderLayout.CENTER);
                packImagePanel.add(packBrowseButton, BorderLayout.EAST);
                panel.add(packImagePanel);
                break;
        }
        return panel;
    }

    private void loadDestinations(JComboBox<String> comboBox) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom FROM Destination");
            comboBox.addItem("Sélectionner une destination");
            while (rs.next()) {
                comboBox.addItem(rs.getInt("id") + ": " + rs.getString("nom"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des destinations: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHotels(JComboBox<String> comboBox) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom FROM Hotel");
            comboBox.addItem("Sélectionner un hôtel");
            while (rs.next()) {
                comboBox.addItem(rs.getInt("id") + ": " + rs.getString("nom"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des hôtels: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRestaurants(JComboBox<String> comboBox) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom FROM Restaurant");
            comboBox.addItem("Sélectionner un restaurant");
            while (rs.next()) {
                comboBox.addItem(rs.getInt("id") + ": " + rs.getString("nom"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des restaurants: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddEntityDialog(String entityType, Object[] entityData, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, entityData == null ? "Ajouter " + entityType : "Modifier " + entityType, true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        List<JComponent> fields = new ArrayList<>();
        
        switch (entityType) {
            case "destination":
                panel.add(new JLabel("Nom:"), gbc);
                gbc.gridx++;
                JTextField nomField = new JTextField(20);
                if (entityData != null) nomField.setText((String) entityData[1]);
                panel.add(nomField, gbc);
                fields.add(nomField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Date (dd/mm/yyyy):"), gbc);
                gbc.gridx++;
                JTextField dateField = new JTextField(20);
                if (entityData != null) dateField.setText((String) entityData[2]);
                panel.add(dateField, gbc);
                fields.add(dateField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Prix:"), gbc);
                gbc.gridx++;
                JTextField prixField = new JTextField(20);
                if (entityData != null) prixField.setText(entityData[3].toString());
                panel.add(prixField, gbc);
                fields.add(prixField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description courte:"), gbc);
                gbc.gridx++;
                JTextArea descCourteArea = new JTextArea(3, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT descriptionCourte FROM Destination WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) descCourteArea.setText(rs.getString("descriptionCourte"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(descCourteArea), gbc);
                fields.add(descCourteArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description:"), gbc);
                gbc.gridx++;
                JTextArea descArea = new JTextArea(5, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT description FROM Destination WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) descArea.setText(rs.getString("description"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(descArea), gbc);
                fields.add(descArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Disponible:"), gbc);
                gbc.gridx++;
                JCheckBox disponibleCheck = new JCheckBox();
                if (entityData != null) disponibleCheck.setSelected((Boolean) entityData[4]);
                panel.add(disponibleCheck, gbc);
                fields.add(disponibleCheck);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Image:"), gbc);
                gbc.gridx++;
                JTextField imagePathField = new JTextField(20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT imagePath FROM Destination WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) imagePathField.setText(rs.getString("imagePath"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                JButton browseButton = new JButton("Parcourir");
                browseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
                    fileChooser.setFileFilter(filter);
                    if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                        imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel imagePanel = new JPanel(new BorderLayout());
                imagePanel.add(imagePathField, BorderLayout.CENTER);
                imagePanel.add(browseButton, BorderLayout.EAST);
                panel.add(imagePanel, gbc);
                fields.add(imagePathField);
                break;
                
            case "hotel":
                panel.add(new JLabel("Nom:"), gbc);
                gbc.gridx++;
                JTextField hotelNomField = new JTextField(20);
                if (entityData != null) hotelNomField.setText((String) entityData[1]);
                panel.add(hotelNomField, gbc);
                fields.add(hotelNomField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Étoiles:"), gbc);
                gbc.gridx++;
                JTextField starsField = new JTextField(20);
                if (entityData != null) starsField.setText(entityData[2].toString());
                panel.add(starsField, gbc);
                fields.add(starsField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Prix:"), gbc);
                gbc.gridx++;
                JTextField hotelPrixField = new JTextField(20);
                if (entityData != null) hotelPrixField.setText(entityData[3].toString());
                panel.add(hotelPrixField, gbc);
                fields.add(hotelPrixField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description courte:"), gbc);
                gbc.gridx++;
                JTextArea hotelDescCourteArea = new JTextArea(3, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT descriptionCourte FROM Hotel WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) hotelDescCourteArea.setText(rs.getString("descriptionCourte"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(hotelDescCourteArea), gbc);
                fields.add(hotelDescCourteArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description:"), gbc);
                gbc.gridx++;
                JTextArea hotelDescArea = new JTextArea(5, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT description FROM Hotel WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) hotelDescArea.setText(rs.getString("description"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(hotelDescArea), gbc);
                fields.add(hotelDescArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Destination:"), gbc);
                gbc.gridx++;
                JComboBox<String> destCombo = new JComboBox<>();
                loadDestinations(destCombo);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT destination_id FROM Hotel WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            int destId = rs.getInt("destination_id");
                            for (int i = 0; i < destCombo.getItemCount(); i++) {
                                if (destCombo.getItemAt(i).startsWith(destId + ":")) {
                                    destCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(destCombo, gbc);
                fields.add(destCombo);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Image:"), gbc);
                gbc.gridx++;
                JTextField hotelImagePathField = new JTextField(20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT imagePath FROM Hotel WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) hotelImagePathField.setText(rs.getString("imagePath"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                JButton hotelBrowseButton = new JButton("Parcourir");
                hotelBrowseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
                    fileChooser.setFileFilter(filter);
                    if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                        hotelImagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel hotelImagePanel = new JPanel(new BorderLayout());
                hotelImagePanel.add(hotelImagePathField, BorderLayout.CENTER);
                hotelImagePanel.add(hotelBrowseButton, BorderLayout.EAST);
                panel.add(hotelImagePanel, gbc);
                fields.add(hotelImagePathField);
                break;
                
            case "restaurant":
                panel.add(new JLabel("Nom:"), gbc);
                gbc.gridx++;
                JTextField restNomField = new JTextField(20);
                if (entityData != null) restNomField.setText((String) entityData[1]);
                panel.add(restNomField, gbc);
                fields.add(restNomField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Cuisine:"), gbc);
                gbc.gridx++;
                JTextField cuisineField = new JTextField(20);
                if (entityData != null) cuisineField.setText((String) entityData[2]);
                panel.add(cuisineField, gbc);
                fields.add(cuisineField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Prix moyen:"), gbc);
                gbc.gridx++;
                JTextField restPrixField = new JTextField(20);
                if (entityData != null) restPrixField.setText(entityData[3].toString());
                panel.add(restPrixField, gbc);
                fields.add(restPrixField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description courte:"), gbc);
                gbc.gridx++;
                JTextArea restDescCourteArea = new JTextArea(3, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT descriptionCourte FROM Restaurant WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) restDescCourteArea.setText(rs.getString("descriptionCourte"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(restDescCourteArea), gbc);
                fields.add(restDescCourteArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description:"), gbc);
                gbc.gridx++;
                JTextArea restDescArea = new JTextArea(5, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT description FROM Restaurant WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) restDescArea.setText(rs.getString("description"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(restDescArea), gbc);
                fields.add(restDescArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Destination:"), gbc);
                gbc.gridx++;
                JComboBox<String> restDestCombo = new JComboBox<>();
                loadDestinations(restDestCombo);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT destination_id FROM Restaurant WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            int destId = rs.getInt("destination_id");
                            for (int i = 0; i < restDestCombo.getItemCount(); i++) {
                                if (restDestCombo.getItemAt(i).startsWith(destId + ":")) {
                                    restDestCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(restDestCombo, gbc);
                fields.add(restDestCombo);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Image:"), gbc);
                gbc.gridx++;
                JTextField restImagePathField = new JTextField(20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT imagePath FROM Restaurant WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) restImagePathField.setText(rs.getString("imagePath"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                JButton restBrowseButton = new JButton("Parcourir");
                restBrowseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
                    fileChooser.setFileFilter(filter);
                    if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                        restImagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel restImagePanel = new JPanel(new BorderLayout());
                restImagePanel.add(restImagePathField, BorderLayout.CENTER);
                restImagePanel.add(restBrowseButton, BorderLayout.EAST);
                panel.add(restImagePanel, gbc);
                fields.add(restImagePathField);
                break;
                
            case "packageVoyage":
                panel.add(new JLabel("Nom:"), gbc);
                gbc.gridx++;
                JTextField packNomField = new JTextField(20);
                if (entityData != null) packNomField.setText((String) entityData[1]);
                panel.add(packNomField, gbc);
                fields.add(packNomField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Prix:"), gbc);
                gbc.gridx++;
                JTextField packPrixField = new JTextField(20);
                if (entityData != null) packPrixField.setText(entityData[2].toString());
                panel.add(packPrixField, gbc);
                fields.add(packPrixField);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description courte:"), gbc);
                gbc.gridx++;
                JTextArea packDescCourteArea = new JTextArea(3, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT descriptionCourte FROM PackageVoyage WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) packDescCourteArea.setText(rs.getString("descriptionCourte"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(packDescCourteArea), gbc);
                fields.add(packDescCourteArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Description:"), gbc);
                gbc.gridx++;
                JTextArea packDescArea = new JTextArea(5, 20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT description FROM PackageVoyage WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) packDescArea.setText(rs.getString("description"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(new JScrollPane(packDescArea), gbc);
                fields.add(packDescArea);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Destination:"), gbc);
                gbc.gridx++;
                JComboBox<String> packDestCombo = new JComboBox<>();
                loadDestinations(packDestCombo);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT destination_id FROM PackageVoyage WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            int destId = rs.getInt("destination_id");
                            for (int i = 0; i < packDestCombo.getItemCount(); i++) {
                                if (packDestCombo.getItemAt(i).startsWith(destId + ":")) {
                                    packDestCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(packDestCombo, gbc);
                fields.add(packDestCombo);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Hôtel:"), gbc);
                gbc.gridx++;
                JComboBox<String> packHotelCombo = new JComboBox<>();
                loadHotels(packHotelCombo);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT hotel_id FROM PackageVoyage WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            int hotelId = rs.getInt("hotel_id");
                            for (int i = 0; i < packHotelCombo.getItemCount(); i++) {
                                if (packHotelCombo.getItemAt(i).startsWith(hotelId + ":")) {
                                    packHotelCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(packHotelCombo, gbc);
                fields.add(packHotelCombo);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Restaurant:"), gbc);
                gbc.gridx++;
                JComboBox<String> packRestCombo = new JComboBox<>();
                loadRestaurants(packRestCombo);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT restaurant_id FROM PackageVoyage WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            int restId = rs.getInt("restaurant_id");
                            for (int i = 0; i < packRestCombo.getItemCount(); i++) {
                                if (packRestCombo.getItemAt(i).startsWith(restId + ":")) {
                                    packRestCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                panel.add(packRestCombo, gbc);
                fields.add(packRestCombo);
                
                gbc.gridx = 0; gbc.gridy++;
                panel.add(new JLabel("Image:"), gbc);
                gbc.gridx++;
                JTextField packImagePathField = new JTextField(20);
                if (entityData != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement("SELECT imagePath FROM PackageVoyage WHERE id = ?");
                        pstmt.setInt(1, (Integer) entityData[0]);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) packImagePathField.setText(rs.getString("imagePath"));
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                JButton packBrowseButton = new JButton("Parcourir");
                packBrowseButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
                    fileChooser.setFileFilter(filter);
                    if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                        packImagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
                JPanel packImagePanel = new JPanel(new BorderLayout());
                packImagePanel.add(packImagePathField, BorderLayout.CENTER);
                packImagePanel.add(packBrowseButton, BorderLayout.EAST);
                panel.add(packImagePanel, gbc);
                fields.add(packImagePathField);
                break;
        }
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(TEAL_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            if (saveEntity(entityType, fields, entityData, model)) {
                dialog.dispose();
            }
        });
        
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private boolean saveEntity(String entityType, List<JComponent> fields, Object[] entityData, DefaultTableModel model) {
        try {
            switch (entityType) {
                case "destination":
                    String nom = ((JTextField) fields.get(0)).getText().trim();
                    String date = ((JTextField) fields.get(1)).getText().trim();
                    String prixText = ((JTextField) fields.get(2)).getText().trim();
                    String descCourte = ((JTextArea) fields.get(3)).getText().trim();
                    String desc = ((JTextArea) fields.get(4)).getText().trim();
                    boolean disponible = ((JCheckBox) fields.get(5)).isSelected();
                    String imagePath = ((JTextField) fields.get(6)).getText().trim();
                    
                    if (nom.isEmpty() || date.isEmpty() || prixText.isEmpty() || descCourte.isEmpty() || desc.isEmpty() || imagePath.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Tous les champs sont requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    double prix = Double.parseDouble(prixText);
                    
                    if (entityData == null) {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO Destination (nom, date, prix, descriptionCourte, description, disponible, imagePath) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                        pstmt.setString(1, nom);
                        pstmt.setString(2, date);
                        pstmt.setDouble(3, prix);
                        pstmt.setString(4, descCourte);
                        pstmt.setString(5, desc);
                        pstmt.setBoolean(6, disponible);
                        pstmt.setString(7, imagePath);
                        pstmt.executeUpdate();
                        
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            model.addRow(new Object[]{
                                rs.getInt(1),
                                nom,
                                date,
                                prix,
                                disponible
                            });
                        }
                        rs.close();
                        pstmt.close();
                    } else {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE Destination SET nom = ?, date = ?, prix = ?, descriptionCourte = ?, " +
                            "description = ?, disponible = ?, imagePath = ? WHERE id = ?");
                        pstmt.setString(1, nom);
                        pstmt.setString(2, date);
                        pstmt.setDouble(3, prix);
                        pstmt.setString(4, descCourte);
                        pstmt.setString(5, desc);
                        pstmt.setBoolean(6, disponible);
                        pstmt.setString(7, imagePath);
                        pstmt.setInt(8, (Integer) entityData[0]);
                        pstmt.executeUpdate();
                        pstmt.close();
                        
                        int row = findRowInModel(model, (Integer) entityData[0]);
                        if (row >= 0) {
                            model.setValueAt(nom, row, 1);
                            model.setValueAt(date, row, 2);
                            model.setValueAt(prix, row, 3);
                            model.setValueAt(disponible, row, 4);
                        }
                    }
                    break;
                    
                case "hotel":
                    String hotelNom = ((JTextField) fields.get(0)).getText().trim();
                    String starsText = ((JTextField) fields.get(1)).getText().trim();
                    String hotelPrixText = ((JTextField) fields.get(2)).getText().trim();
                    String hotelDescCourte = ((JTextArea) fields.get(3)).getText().trim();
                    String hotelDesc = ((JTextArea) fields.get(4)).getText().trim();
                    String destSelection = ((JComboBox<String>) fields.get(5)).getSelectedItem().toString();
                    String hotelImagePath = ((JTextField) fields.get(6)).getText().trim();
                    
                    if (hotelNom.isEmpty() || starsText.isEmpty() || hotelPrixText.isEmpty() || 
                        hotelDescCourte.isEmpty() || hotelDesc.isEmpty() || hotelImagePath.isEmpty() || 
                        destSelection.equals("Sélectionner une destination")) {
                        JOptionPane.showMessageDialog(this, "Tous les champs sont requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    int stars = Integer.parseInt(starsText);
                    double hotelPrix = Double.parseDouble(hotelPrixText);
                    int destId = Integer.parseInt(destSelection.split(":")[0]);
                    
                    if (entityData == null) {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO Hotel (destination_id, nom, stars, prix, descriptionCourte, description, imagePath) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                        pstmt.setInt(1, destId);
                        pstmt.setString(2, hotelNom);
                        pstmt.setInt(3, stars);
                        pstmt.setDouble(4, hotelPrix);
                        pstmt.setString(5, hotelDescCourte);
                        pstmt.setString(6, hotelDesc);
                        pstmt.setString(7, hotelImagePath);
                        pstmt.executeUpdate();
                        
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            model.addRow(new Object[]{
                                rs.getInt(1),
                                hotelNom,
                                stars,
                                hotelPrix,
                                destSelection.split(": ")[1]
                            });
                        }
                        rs.close();
                        pstmt.close();
                    } else {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE Hotel SET destination_id = ?, nom = ?, stars = ?, prix = ?, " +
                            "descriptionCourte = ?, description = ?, imagePath = ? WHERE id = ?");
                        pstmt.setInt(1, destId);
                        pstmt.setString(2, hotelNom);
                        pstmt.setInt(3, stars);
                        pstmt.setDouble(4, hotelPrix);
                        pstmt.setString(5, hotelDescCourte);
                        pstmt.setString(6, hotelDesc);
                        pstmt.setString(7, hotelImagePath);
                        pstmt.setInt(8, (Integer) entityData[0]);
                        pstmt.executeUpdate();
                        pstmt.close();
                        
                        int row = findRowInModel(model, (Integer) entityData[0]);
                        if (row >= 0) {
                            model.setValueAt(hotelNom, row, 1);
                            model.setValueAt(stars, row, 2);
                            model.setValueAt(hotelPrix, row, 3);
                            model.setValueAt(destSelection.split(": ")[1], row, 4);
                        }
                    }
                    break;
                    
                case "restaurant":
                    String restNom = ((JTextField) fields.get(0)).getText().trim();
                    String cuisine = ((JTextField) fields.get(1)).getText().trim();
                    String restPrixText = ((JTextField) fields.get(2)).getText().trim();
                    String restDescCourte = ((JTextArea) fields.get(3)).getText().trim();
                    String restDesc = ((JTextArea) fields.get(4)).getText().trim();
                    String restDestSelection = ((JComboBox<String>) fields.get(5)).getSelectedItem().toString();
                    String restImagePath = ((JTextField) fields.get(6)).getText().trim();
                    
                    if (restNom.isEmpty() || cuisine.isEmpty() || restPrixText.isEmpty() || 
                        restDescCourte.isEmpty() || restDesc.isEmpty() || restImagePath.isEmpty() || 
                        restDestSelection.equals("Sélectionner une destination")) {
                        JOptionPane.showMessageDialog(this, "Tous les champs sont requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    double restPrix = Double.parseDouble(restPrixText);
                    int restDestId = Integer.parseInt(restDestSelection.split(":")[0]);
                    
                    if (entityData == null) {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO Restaurant (destination_id, nom, cuisine, prixMoyen, descriptionCourte, description, imagePath) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                        pstmt.setInt(1, restDestId);
                        pstmt.setString(2, restNom);
                        pstmt.setString(3, cuisine);
                        pstmt.setDouble(4, restPrix);
                        pstmt.setString(5, restDescCourte);
                        pstmt.setString(6, restDesc);
                        pstmt.setString(7, restImagePath);
                        pstmt.executeUpdate();
                        
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            model.addRow(new Object[]{
                                rs.getInt(1),
                                restNom,
                                cuisine,
                                restPrix,
                                restDestSelection.split(": ")[1]
                            });
                        }
                        rs.close();
                        pstmt.close();
                    } else {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE Restaurant SET destination_id = ?, nom = ?, cuisine = ?, prixMoyen = ?, " +
                            "descriptionCourte = ?, description = ?, imagePath = ? WHERE id = ?");
                        pstmt.setInt(1, restDestId);
                        pstmt.setString(2, restNom);
                        pstmt.setString(3, cuisine);
                        pstmt.setDouble(4, restPrix);
                        pstmt.setString(5, restDescCourte);
                        pstmt.setString(6, restDesc);
                        pstmt.setString(7, restImagePath);
                        pstmt.setInt(8, (Integer) entityData[0]);
                        pstmt.executeUpdate();
                        pstmt.close();
                        
                        int row = findRowInModel(model, (Integer) entityData[0]);
                        if (row >= 0) {
                            model.setValueAt(restNom, row, 1);
                            model.setValueAt(cuisine, row, 2);
                            model.setValueAt(restPrix, row, 3);
                            model.setValueAt(restDestSelection.split(": ")[1], row, 4);
                        }
                    }
                    break;
                    
                case "packageVoyage":
                    String packNom = ((JTextField) fields.get(0)).getText().trim();
                    String packPrixText = ((JTextField) fields.get(1)).getText().trim();
                    String packDescCourte = ((JTextArea) fields.get(2)).getText().trim();
                    String packDesc = ((JTextArea) fields.get(3)).getText().trim();
                    String packDestSelection = ((JComboBox<String>) fields.get(4)).getSelectedItem().toString();
                    String packHotelSelection = ((JComboBox<String>) fields.get(5)).getSelectedItem().toString();
                    String packRestSelection = ((JComboBox<String>) fields.get(6)).getSelectedItem().toString();
                    String packImagePath = ((JTextField) fields.get(7)).getText().trim();
                    
                    if (packNom.isEmpty() || packPrixText.isEmpty() || packDescCourte.isEmpty() || 
                        packDesc.isEmpty() || packImagePath.isEmpty() || 
                        packDestSelection.equals("Sélectionner une destination") || 
                        packHotelSelection.equals("Sélectionner un hôtel") || 
                        packRestSelection.equals("Sélectionner un restaurant")) {
                        JOptionPane.showMessageDialog(this, "Tous les champs sont requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    double packPrix = Double.parseDouble(packPrixText);
                    int packDestId = Integer.parseInt(packDestSelection.split(":")[0]);
                    int packHotelId = Integer.parseInt(packHotelSelection.split(":")[0]);
                    int packRestId = Integer.parseInt(packRestSelection.split(":")[0]);
                    
                    if (entityData == null) {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO PackageVoyage (nom, prix, descriptionCourte, description, imagePath, destination_id, hotel_id, restaurant_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                        pstmt.setString(1, packNom);
                        pstmt.setDouble(2, packPrix);
                        pstmt.setString(3, packDescCourte);
                        pstmt.setString(4, packDesc);
                        pstmt.setString(5, packImagePath);
                        pstmt.setInt(6, packDestId);
                        pstmt.setInt(7, packHotelId);
                        pstmt.setInt(8, packRestId);
                        pstmt.executeUpdate();
                        
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            model.addRow(new Object[]{
                                rs.getInt(1),
                                packNom,
                                packPrix,
                                packDestSelection.split(": ")[1],
                                packHotelSelection.split(": ")[1],
                                packRestSelection.split(": ")[1]
                            });
                        }
                        rs.close();
                        pstmt.close();
                    } else {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE PackageVoyage SET nom = ?, prix = ?, descriptionCourte = ?, description = ?, " +
                            "imagePath = ?, destination_id = ?, hotel_id = ?, restaurant_id = ? WHERE id = ?");
                        pstmt.setString(1, packNom);
                        pstmt.setDouble(2, packPrix);
                        pstmt.setString(3, packDescCourte);
                        pstmt.setString(4, packDesc);
                        pstmt.setString(5, packImagePath);
                        pstmt.setInt(6, packDestId);
                        pstmt.setInt(7, packHotelId);
                        pstmt.setInt(8, packRestId);
                        pstmt.setInt(9, (Integer) entityData[0]);
                        pstmt.executeUpdate();
                        pstmt.close();
                        
                        int row = findRowInModel(model, (Integer) entityData[0]);
                        if (row >= 0) {
                            model.setValueAt(packNom, row, 1);
                            model.setValueAt(packPrix, row, 2);
                            model.setValueAt(packDestSelection.split(": ")[1], row, 3);
                            model.setValueAt(packHotelSelection.split(": ")[1], row, 4);
                            model.setValueAt(packRestSelection.split(": ")[1], row, 5);
                        }
                    }
                    break;
            }
            
            JOptionPane.showMessageDialog(this, "Opération réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les champs appropriés", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private int findRowInModel(DefaultTableModel model, int id) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (((Integer) model.getValueAt(i, 0)) == id) {
                return i;
            }
        }
        return -1;
    }

    private void deleteEntity(String entityType, JTable table, int selectedRow, DefaultTableModel model) {
        int id = (Integer) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Êtes-vous sûr de vouloir supprimer cet élément? Cela peut affecter d'autres entités liées.", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String tableName = "";
                switch (entityType) {
                    case "destination": tableName = "Destination"; break;
                    case "hotel": tableName = "Hotel"; break;
                    case "restaurant": tableName = "Restaurant"; break;
                    case "packageVoyage": tableName = "PackageVoyage"; break;
                }
                
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                pstmt.close();
                
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Suppression réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
   private void showVisualization() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel visuPanel = new JPanel(new BorderLayout());
        visuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet Packages
        JPanel packagePanel = new JPanel(new BorderLayout());
        packagePanel.add(createChartPanel("package", "Packages"), BorderLayout.CENTER);
        tabbedPane.addTab("Packages", packagePanel);

        // Onglet Hôtels
        JPanel hotelPanel = new JPanel(new BorderLayout());
        hotelPanel.add(createChartPanel("hotel", "Hôtels"), BorderLayout.CENTER);
        tabbedPane.addTab("Hôtels", hotelPanel);

        // Onglet Restaurants
        JPanel restPanel = new JPanel(new BorderLayout());
        restPanel.add(createChartPanel("restaurant", "Restaurants"), BorderLayout.CENTER);
        tabbedPane.addTab("Restaurants", restPanel);

        visuPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.add(visuPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private ChartPanel createChartPanel(String entityType, String title) {
        DefaultCategoryDataset dataset = createDataset(entityType);
        JFreeChart chart = ChartFactory.createBarChart(
            "Réservations pour " + title,
            "Item",
            "Nombre de réservations",
            dataset,
            PlotOrientation.VERTICAL,
            true, 
            true,
            false
        );
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, TEAL_COLOR); 
        chart.getCategoryPlot().getRenderer().setSeriesPaint(1, Color.GRAY); 
        return new ChartPanel(chart);
    }

    private DefaultCategoryDataset createDataset(String entityType) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> reservedCounts = getReservationCounts(entityType);
        Map<String, Integer> nonReservedCounts = getNonReservedCounts(entityType);

      
        for (Map.Entry<String, Integer> entry : reservedCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Réservé", entry.getKey());
        }

        for (Map.Entry<String, Integer> entry : nonReservedCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Non Réservé", entry.getKey());
        }

        return dataset;
    }

    private Map<String, Integer> getReservationCounts(String entityType) {
        Map<String, Integer> counts = new HashMap<>();
        String typeName;

        switch (entityType) {
            case "package":
                typeName = "Package";
                break;
            case "hotel":
                typeName = "Hôtel";
                break;
            case "restaurant":
                typeName = "Restaurant";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Type d'entité inconnu: " + entityType, "Erreur", JOptionPane.ERROR_MESSAGE);
                return counts;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT nom_item, COUNT(*) as count FROM reservation WHERE type = ? GROUP BY nom_item"
            );
            pstmt.setString(1, typeName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                counts.put(rs.getString("nom_item"), rs.getInt("count"));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la récupération des réservations pour " + typeName + ": " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        return counts;
    }

    private Map<String, Integer> getNonReservedCounts(String entityType) {
        Map<String, Integer> nonReserved = new HashMap<>();
        String tableName;
        String typeName;

        switch (entityType) {
            case "package":
                tableName = "packageVoyage";
                typeName = "Package";
                break;
            case "hotel":
                tableName = "hotel";
                typeName = "Hôtel";
                break;
            case "restaurant":
                tableName = "restaurant";
                typeName = "Restaurant";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Type d'entité inconnu: " + entityType, "Erreur", JOptionPane.ERROR_MESSAGE);
                return nonReserved;
        }

        try {
            
            String query = "SELECT t.nom, COUNT(r.nom_item) as reservation_count " +
                          "FROM " + tableName + " t " +
                          "LEFT JOIN reservation r ON t.nom = r.nom_item AND r.type = ? " +
                          "GROUP BY t.nom";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, typeName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String itemName = rs.getString("nom");
                int reservationCount = rs.getInt("reservation_count");
                if (reservationCount == 0) {
                    nonReserved.put(itemName, 0); 
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la récupération des non-réservés pour " + typeName + ": " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        return nonReserved;
    }

private void showAddDestination() {
    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    JPanel addPanel = new JPanel(new BorderLayout());
    addPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTabbedPane tabbedPane = new JTabbedPane();
    
    JPanel destPanel = createEntityFormPanel("Destination");
    tabbedPane.addTab("Destination", destPanel);
    

    JPanel hotelPanel = createEntityFormPanel("Hôtel");
    tabbedPane.addTab("Hôtel", hotelPanel);
    
   
    JPanel restPanel = createEntityFormPanel("Restaurant");
    tabbedPane.addTab("Restaurant", restPanel);

    JPanel packPanel = createEntityFormPanel("Package");
    tabbedPane.addTab("Package", packPanel);

    addPanel.add(tabbedPane, BorderLayout.CENTER);
    contentPanel.add(addPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}

private JPanel createEntityFormPanel(String entityType) {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 5);

    
    panel.add(new JLabel("Formulaire pour " + entityType), gbc);
    
   
    gbc.gridy++;
    panel.add(new JLabel("Nom:"), gbc);
    gbc.gridx++;
    panel.add(new JTextField(20), gbc);
    
  

    JButton saveButton = new JButton("Enregistrer");
    saveButton.setBackground(TEAL_COLOR);
    saveButton.setForeground(Color.WHITE);
    
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.CENTER;
    panel.add(saveButton, gbc);

    return panel;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard());
    }
}

class Destination {
    private int id;
    private String nom;
    private String date;
    private double prix;
    private String descriptionCourte;
    private String description;
    private boolean disponible;
    private String imagePath;
    private List<Hotel> hotels;
    private List<Restaurant> restaurants;

    public Destination(int id, String nom, String date, double prix, String descriptionCourte,
                       String description, boolean disponible, String imagePath) {
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.prix = prix;
        this.descriptionCourte = descriptionCourte;
        this.description = description;
        this.disponible = disponible;
        this.imagePath = imagePath;
        this.hotels = new ArrayList<>();
        this.restaurants = new ArrayList<>();
    }

    public void addHotel(Hotel hotel) {
        this.hotels.add(hotel);
    }

    public void addRestaurant(Restaurant restaurant) {
        this.restaurants.add(restaurant);
    }

    public List<Hotel> getHotels() {
        return hotels;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDate() { return date; }
    public double getPrix() { return prix; }
    public String getDescriptionCourte() { return descriptionCourte; }
    public String getDescription() { return description; }
    public boolean isDisponible() { return disponible; }
    public String getImagePath() { return imagePath; }

    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}

class Hotel {
    private int id;
    private String nom;
    private int stars;
    private double prix;
    private String descriptionCourte;
    private String description;
    private String imagePath;

    public Hotel(int id, String nom, int stars, double prix, String descriptionCourte,
                 String description, String imagePath) {
        this.id = id;
        this.nom = nom;
        this.stars = stars;
        this.prix = prix;
        this.descriptionCourte = descriptionCourte;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public int getStars() { return stars; }
    public double getPrix() { return prix; }
    public String getDescriptionCourte() { return descriptionCourte; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }

    public void setStars(int stars) { this.stars = stars; }
    public void setPrix(double prix) { this.prix = prix; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }
    public void setDescription(String description) { this.description = description; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}

class Restaurant {
    private int id;
    private String nom;
    private String cuisine;
    private double prixMoyen;
    private String descriptionCourte;
    private String description;
    private String imagePath;

    public Restaurant(int id, String nom, String cuisine, double prixMoyen,
                      String descriptionCourte, String description, String imagePath) {
        this.id = id;
        this.nom = nom;
        this.cuisine = cuisine;
        this.prixMoyen = prixMoyen;
        this.descriptionCourte = descriptionCourte;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getCuisine() { return cuisine; }
    public double getPrixMoyen() { return prixMoyen; }
    public String getDescriptionCourte() { return descriptionCourte; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }

    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public void setPrixMoyen(double prixMoyen) { this.prixMoyen = prixMoyen; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }
    public void setDescription(String description) { this.description = description; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}

class PackageVoyage {
    private int id;
    private String nom;
    private double prix;
    private String descriptionCourte;
    private String description;
    private String imagePath;
    private Destination destination;
    private Hotel hotel;
    private Restaurant restaurant;

    public PackageVoyage(int id, String nom, double prix, String descriptionCourte,
                         String description, String imagePath,
                         Destination destination, Hotel hotel, Restaurant restaurant) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.descriptionCourte = descriptionCourte;
        this.description = description;
        this.imagePath = imagePath;
        this.destination = destination;
        this.hotel = hotel;
        this.restaurant = restaurant;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getPrix() { return prix; }
    public String getDescriptionCourte() { return descriptionCourte; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
    public Destination getDestination() { return destination; }
    public Hotel getHotel() { return hotel; }
    public Restaurant getRestaurant() { return restaurant; }
}