(ns hospital.aula2
  (:use clojure.pprint))

;(defrecord Paciente [id, nome, nascimento])

; Paciente com Plano de Saúde ===> + plano de saúde
; Paciente Particular ===> + 0

; Herança tradicional de OOP não é o recomendado em Clojure
; Seria algo como:
; (defrecord PacientePlanoDeSaúde HERDA Paciente [plano])
; Porém, exige uma alta complexidade (2ˆn)

; Uma possibilidade é criar dois tipos de pacientes diferentes
 (defrecord PacienteParticular [id, nome, nascimento])
 (defrecord PacientePlanoDeSaude [id, nome, nascimento, plano])

; Regras diferentes para tipos diferentes
; deve-assinar-pre-autorizacao?
; Particular ===> valor >= 50
; PlanoDeSaude ===> procedimento NÃO está no plano

;(defn deve-assinar-pre-autorizacao? [paciente procedimento valor]
;  (if (= PacienteParticular (type paciente))
;    (>= valor 50)
;    ; assumindo que existe os dois tipos
;    (if (= PacientePlanoDeSaude (type paciente))
;      (let [plano (get paciente :plano)]
;        (not (some #(= % procedimento) plano)))
;      true)))

; Equivalente à interface em Java
; Contém uma função
(defprotocol Cobravel
  (deve-assinar-pre-autorizacao? [paciente procedimento valor]))

(extend-type PacienteParticular
  Cobravel
  (deve-assinar-pre-autorizacao? [paciente, procedimento, valor]
    (>= valor 50)))

(extend-type PacientePlanoDeSaude
  Cobravel
  (deve-assinar-pre-autorizacao? [paciente, procedimento, valor]
    (let [plano (:plano paciente)]
      (not (some #(= % procedimento) plano)))))
; contains => verifica o índice, e o índice fica dependente da estrutura de dados

; Alternativa seria implementar diretamente
; (defrecord PacientePlanoDeSaude
;   [id, nome, nascimento, plano]
;   Cobravel
;   (deve-assinar-pre-autorizacao? [paciente, procedimento, valor]
;     (let [plano (:plano paciente)]
;       (not(some #(= % procedimento) plano)))))

; Para 'PacientePlanoDeSaude', além dos parâmetros padrão, passamos os procedimentos cobertos pelo plano
  (let [particular (->PacienteParticular 15, "Guilherme", "18/09/1981")
        plano (->PacientePlanoDeSaude 16, "Lucia", "24/10/1991", [:raio-x, :ultrassom])]
    (pprint (deve-assinar-pre-autorizacao? particular, :raio-x, 500))
    (pprint (deve-assinar-pre-autorizacao? particular, :raio-x, 40))
    (pprint (deve-assinar-pre-autorizacao? plano, :raio-x, 499990))
    (pprint (deve-assinar-pre-autorizacao? plano, :coleta-de-sangue, 499990))
    (pprint (deve-assinar-pre-autorizacao? plano, :ultrassom, 499990))
    )



; Implementando classes que já existem (no Java) e extendemos o comportamento dessas classes/record

; Como transformar data em números -> há diversas formas

(defprotocol Dateable
  (to-ms [this]))

; Uma variação baseada na palestra a seguir, mas com 'extend-type' e com 'gregoriancalendar'
; From Sean Devlin's talk on protocols at Clojure

; Utilizando 'to-ms' (conversão em milissegundos) com as classes Java

(extend-type java.lang.Number
  Dateable
  (to-ms [this] this))

(print "to-ms com Dateable: ")
(pprint (to-ms 56))


(extend-type java.util.Date
  Dateable
  (to-ms [this] (.getTime this)))

(print "to-ms com Dateable e java.util.Date: ")
(pprint (to-ms (java.util.Date.)))                          ; data em milissegundos com momento atual


(extend-type java.util.Calendar
  Dateable
  (to-ms [this] (to-ms (.getTime this))))

(print "to-ms com Dateable e java.util.Calendar: ")
(pprint (to-ms (java.util.Calendar)))


(pprint (to-ms (java.util.GregorianCalendar.)))
(print "to-ms com Dateable e java. util.GregorianCalendar: ")
(pprint (to-ms "Guilherme"))