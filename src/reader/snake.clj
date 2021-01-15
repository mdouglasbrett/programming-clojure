; START: namespace
(ns reader.snake
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener))
  (:use examples.import-static))

(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

; END: namespace

; TODO: implement the Snake!
(def width 75)
(def height 50)
(def point-size 10)
(def turn-millis 75)
(def win-length 5)
(def dirs
  {VK_LEFT  [-1 0]
   VK_RIGHT [1 0]
   VK_UP    [0 -1]
   VK_DOWN  [0 1]})
