/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import algorithm.Recommender;
import javax.swing.*;
import java.util.List;
/**
 *
 * @author macbookpro
 */
public class MovieRatingPanel extends javax.swing.JPanel {
    private Recommender recommender;
    /**
     * Creates new form MovieRatingPanel
     */
    public MovieRatingPanel(Recommender recommender) {
      this.recommender = recommender;

      initComponents();
      
      applyTheme();
      
      loadMovies();

      recommendButton.addActionListener(e -> getRecommendations());
        
    }
    
  private void applyTheme() {

    UIStyle.stylePanel(this);

    UIStyle.styleButton(recommendButton);

    UIStyle.styleComboBox(movieCombo1);
    UIStyle.styleComboBox(movieCombo2);
    UIStyle.styleComboBox(movieCombo3);
    UIStyle.styleComboBox(movieCombo4);
    UIStyle.styleComboBox(movieCombo5);

    UIStyle.styleTextField(ratingField1);
    UIStyle.styleTextField(ratingField2);
    UIStyle.styleTextField(ratingField3);
    UIStyle.styleTextField(ratingField4);
    UIStyle.styleTextField(ratingField5);

    UIStyle.styleTextField(xField);
    UIStyle.styleTextField(kField);

    UIStyle.styleTextArea(resultArea);
    
    UIStyle.styleLabel(xLabel);
    UIStyle.styleLabel(kLabel);
}
    
    private boolean hasDuplicateMovies() {

    String[] movies = {
        (String) movieCombo1.getSelectedItem(),
        (String) movieCombo2.getSelectedItem(),
        (String) movieCombo3.getSelectedItem(),
        (String) movieCombo4.getSelectedItem(),
        (String) movieCombo5.getSelectedItem()
      };

    for (int i = 0; i < movies.length; i++) {

        for (int j = i + 1; j < movies.length; j++) {

            if (movies[i].equals(movies[j])) {

            JOptionPane.showMessageDialog( this, "\"" + movies[i] + "\" is already selected.\nPlease choose a different movie.");

            return true; }
        }
    }

         return false;
   }
    
    private boolean validateRatings() {

    JTextField[] ratings = new JTextField[]{
            ratingField1,
            ratingField2,
            ratingField3,
            ratingField4,
            ratingField5
    };

    for (JTextField field : ratings) {

        try {

            int rating =
                    Integer.parseInt(field.getText());

            if (rating < 0 || rating > 5) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a number from 0 to 5."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a number from 0 to 5."
            );

            return false;
        }
    }

    return true;
}
    
    private void loadMovies() {

    List<String> movies =
            recommender.getRandomMovieTitles(100);

    JComboBox<String>[] combos = new JComboBox[]{
            movieCombo1,
            movieCombo2,
            movieCombo3,
            movieCombo4,
            movieCombo5
    };

    for (JComboBox<String> combo : combos) {

        combo.removeAllItems();

        for (String movie : movies) {
            combo.addItem(movie);
        }
    }
    }
    
    private void getRecommendations() {
       if (hasDuplicateMovies()) {
           return;
        }

       if (!validateRatings()) {
           return;
        }
       try {

        float[] userVector =
                recommender.createEmptyUserVector();

        addMovieRating(movieCombo1, ratingField1, userVector);
        addMovieRating(movieCombo2, ratingField2, userVector);
        addMovieRating(movieCombo3, ratingField3, userVector);
        addMovieRating(movieCombo4, ratingField4, userVector);
        addMovieRating(movieCombo5, ratingField5, userVector);

        int x = Integer.parseInt(xField.getText());
        int k = Integer.parseInt(kField.getText());

        List<String> recommendations =
                recommender.getRecommendations(userVector, x, k);

        resultArea.setText("");

        for (String movie : recommendations) {
            resultArea.append(movie + "\n");
        }

     } catch (Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "Error: " + ex.getMessage()
        );
      }
     }
    private void addMovieRating(
        JComboBox<String> combo,
        JTextField ratingField,
        float[] userVector ) {

    String movie =
            (String) combo.getSelectedItem();

    if (movie == null ||
            ratingField.getText().isEmpty()) {
        return;
    }

    int rating =
            Integer.parseInt(ratingField.getText());

    int colIndex =
            recommender.getColIndexForTitle(movie);

    if (colIndex != -1) {
        userVector[colIndex] = rating;
    }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        movieCombo1 = new javax.swing.JComboBox<>();
        movieCombo2 = new javax.swing.JComboBox<>();
        movieCombo3 = new javax.swing.JComboBox<>();
        movieCombo4 = new javax.swing.JComboBox<>();
        movieCombo5 = new javax.swing.JComboBox<>();
        ratingField1 = new javax.swing.JTextField();
        ratingField2 = new javax.swing.JTextField();
        ratingField3 = new javax.swing.JTextField();
        ratingField4 = new javax.swing.JTextField();
        ratingField5 = new javax.swing.JTextField();
        xLabel = new javax.swing.JLabel();
        kLabel = new javax.swing.JLabel();
        recommendButton = new javax.swing.JButton();
        xField = new javax.swing.JTextField();
        kField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();

        movieCombo1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        movieCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        movieCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                movieCombo1ActionPerformed(evt);
            }
        });

        movieCombo2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        movieCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        movieCombo3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        movieCombo3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        movieCombo4.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        movieCombo4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        movieCombo5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        movieCombo5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ratingField1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        ratingField2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        ratingField3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        ratingField4.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        ratingField5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        xLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        xLabel.setText("X:");

        kLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        kLabel.setText("K:");

        recommendButton.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        recommendButton.setText("Get Recommendations");

        xField.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        kField.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N

        resultArea.setEditable(false);
        resultArea.setColumns(20);
        resultArea.setLineWrap(true);
        resultArea.setRows(5);
        jScrollPane1.setViewportView(resultArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(movieCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(117, 117, 117))
                                .addComponent(movieCombo4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(movieCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(movieCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(movieCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ratingField2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(ratingField1)
                            .addComponent(ratingField3)
                            .addComponent(ratingField4)
                            .addComponent(ratingField5))
                        .addGap(124, 124, 124)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(kLabel)
                                    .addComponent(xLabel))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(kField, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(recommendButton))))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(ratingField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(movieCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(movieCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ratingField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(movieCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ratingField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(movieCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ratingField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(movieCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ratingField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(xLabel)
                            .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(kLabel)
                            .addComponent(kField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(recommendButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void movieCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_movieCombo1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_movieCombo1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField kField;
    private javax.swing.JLabel kLabel;
    private javax.swing.JComboBox<String> movieCombo1;
    private javax.swing.JComboBox<String> movieCombo2;
    private javax.swing.JComboBox<String> movieCombo3;
    private javax.swing.JComboBox<String> movieCombo4;
    private javax.swing.JComboBox<String> movieCombo5;
    private javax.swing.JTextField ratingField1;
    private javax.swing.JTextField ratingField2;
    private javax.swing.JTextField ratingField3;
    private javax.swing.JTextField ratingField4;
    private javax.swing.JTextField ratingField5;
    private javax.swing.JButton recommendButton;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JTextField xField;
    private javax.swing.JLabel xLabel;
    // End of variables declaration//GEN-END:variables
}
