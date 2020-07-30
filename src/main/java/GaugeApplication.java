 import eu.hansolo.medusa.Gauge;
 import eu.hansolo.medusa.GaugeBuilder;
 import javafx.application.Application;
 import javafx.application.Platform;
 import javafx.beans.property.SimpleDoubleProperty;
 import javafx.geometry.Pos;
 import javafx.scene.Scene;
 import javafx.scene.control.Button;
 import javafx.scene.control.Label;
 import javafx.scene.layout.VBox;
 import javafx.scene.paint.Color;
 import javafx.stage.Stage;

 /**
  * An Application showing
  */
 public class GaugeApplication extends Application
 {
     public static void main(String[] args)
     {
         launch(args);
     }

     @Override
     public void start(Stage primaryStage)
     {
         Gauge progressGauge = GaugeBuilder.create()
                                           .skinType(Gauge.SkinType.BAR)
                                           .barColor(Color.DEEPSKYBLUE)
//                                           .animated(false) // without animation it all works, with animation enabled the issue is experienced
                                           .build();

         SimpleDoubleProperty value = new SimpleDoubleProperty(100);
         progressGauge.valueProperty().bind(value);

         Button button = new Button("Cause Issue");
         button.setOnAction((e) -> {
             new Thread(() -> {
                 causeIssue(value);
             }).start();
         });

         Label label = new Label("Current Value: " + String.valueOf(value.get()));
         value.addListener((o, newValue, oldValue) -> label.setText("Current Value: " + value.get()));

         VBox box = new VBox();
         box.setAlignment(Pos.CENTER);
         box.setSpacing(10);
         box.getChildren().addAll(button, label, progressGauge);

         Scene scene = new Scene(box, 300, 300);
         primaryStage.setScene(scene);
         primaryStage.show();
     }

     /**
      * By updating a value rapidly from 100 -> 50 -> 1, the value displayed only reaches 50 while the "currentValue"
      * is properly set to 1.
      *
      * @param value value to set
      */
     private void causeIssue(SimpleDoubleProperty value)
     {
         try {
             Platform.runLater(() -> value.set(100)); // ensure we start at 100
             Thread.sleep(2000);
             Platform.runLater(() -> value.set(50));
             Thread.sleep(20);
             Platform.runLater(() -> value.set(1));
         }
         catch (InterruptedException e)
         {
             e.printStackTrace();
         }
     }
 }
