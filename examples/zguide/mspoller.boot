#!/usr/bin/env boot

(set-env! :dependencies '[[io.djy/ezzmq "0.5.2"]])

(require '[ezzmq.core :as zmq])

(defn -main
  ([]
   (println "No ports specified.")
   (System/exit 1))
  ([port]
   (println "Only one port specified; need two.")
   (System/exit 1))
  ([port1 port2]
   (zmq/with-new-context
     (let [pull (zmq/socket :pull {:connect (format "tcp://*:%s" port1)})
           sub  (zmq/socket :sub  {:connect (format "tcp://*:%s" port2)
                                   :subscribe "10001"})]
       (zmq/polling {:stringify true}
         [pull :pollin [msg] ; connect to task ventilator
          (println (format "PULL: got msg: %s" msg))

          sub :pollin [msg]  ; connect to weather server
          (println (format "SUB: got msg: %s" msg))]

         (zmq/while-polling
           (zmq/poll)))))))
