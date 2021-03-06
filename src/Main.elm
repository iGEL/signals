module Main exposing (main)

import Browser
import Html exposing (button, div, input, label, option, select, span, table, td, text, th, tr)
import Html.Attributes exposing (checked, disabled, style, type_, value)
import Html.Events exposing (onClick)
import Messages exposing (..)
import SelectChange exposing (..)
import Signal
import SignalModel
import Zs3


type Msg
    = FirstSignalMsg Messages.Msg
    | SecondSignalMsg Messages.Msg
    | SwitchLanguage Language
    | SetSignalType SignalModel.SignalType


type Language
    = German
    | English


type alias Model =
    { distantSignal : SignalModel.Model
    , signalRepeater : SignalModel.Model
    , combinationSignal : SignalModel.Model
    , mainSignal : SignalModel.Model
    , signalType : SignalModel.SignalType
    , language : Language
    }


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , view = view
        , update = update
        , subscriptions = \n -> Sub.none
        }


init : () -> ( Model, Cmd Msg )
init ignore =
    ( { distantSignal = SignalModel.distantSignal
      , signalRepeater = SignalModel.signalRepeater
      , combinationSignal = SignalModel.combinationSignal
      , mainSignal = SignalModel.mainSignal
      , signalType = SignalModel.Ks
      , language = German
      }
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FirstSignalMsg signalmsg ->
            let
                newDistantSignal =
                    Signal.update (ToDistantSignal signalmsg) model.distantSignal

                newSignalRepeater =
                    Signal.update (ToDistantSignal signalmsg) model.signalRepeater

                newCombinationSignal =
                    Signal.update (ToMainSignal signalmsg) model.combinationSignal
            in
            ( { model
                | distantSignal = newDistantSignal
                , signalRepeater = newSignalRepeater
                , combinationSignal = newCombinationSignal
              }
            , Cmd.none
            )

        SecondSignalMsg signalmsg ->
            let
                newCombinationSignal =
                    Signal.update (ToDistantSignal signalmsg) model.combinationSignal

                newMainSignal =
                    Signal.update (ToMainSignal signalmsg) model.mainSignal
            in
            ( { model
                | combinationSignal = newCombinationSignal
                , mainSignal = newMainSignal
              }
            , Cmd.none
            )

        SetSignalType signalType ->
            ( { model | signalType = signalType }, Cmd.none )

        SwitchLanguage language ->
            ( { model | language = language }, Cmd.none )


view : Model -> Html.Html Msg
view model =
    div []
        [ div []
            [ button [ onClick (SwitchLanguage German) ] [ text "Deutsch" ]
            , button [ onClick (SwitchLanguage English) ] [ text "English" ]
            ]
        , div []
            [ translate model "Signaltyp" "Signal type"
            , label []
                [ input
                    [ type_ "radio"
                    , checked (model.signalType == SignalModel.Ks)
                    , onClick (SetSignalType SignalModel.Ks)
                    ]
                    []
                , text "Ks"
                ]
            , label []
                [ input
                    [ type_ "radio"
                    , checked (model.signalType == SignalModel.HvLight)
                    , onClick (SetSignalType SignalModel.HvLight)
                    ]
                    []
                , translate model "H/V Lichtsignal" "H/V light signal"
                ]
            , label []
                [ input
                    [ type_ "radio"
                    , checked (model.signalType == SignalModel.HvSemaphore)
                    , onClick (SetSignalType SignalModel.HvSemaphore)
                    ]
                    []
                , translate model "H/V Formsignal" "H/V semaphore"
                ]
            , label []
                [ input
                    [ type_ "radio"
                    , checked (model.signalType == SignalModel.Hl)
                    , onClick (SetSignalType SignalModel.Hl)
                    ]
                    []
                , text "Hl"
                ]
            ]
        , table [ style "margin" "20px" ]
            [ tr []
                [ th [] [ translate model "Vorsignal" "Distant Signal" ]
                , th [] [ translate model "Vorsignalwiederholer" "Signal Repeater" ]
                , th [] [ translate model "Mehrabschnittssignal" "Combination Signal" ]
                , th [] [ translate model "Hauptsignal" "Main Signal" ]
                ]
            , tr []
                [ td []
                    [ distantSignalOptions FirstSignalMsg model.distantSignal model
                    ]
                , td [] []
                , td [ style "text-align" "center" ]
                    [ mainSignalOptions FirstSignalMsg model.combinationSignal model
                    , distantSignalOptions SecondSignalMsg model.combinationSignal model
                    ]
                , td [ style "text-align" "center" ]
                    [ mainSignalOptions SecondSignalMsg model.mainSignal model ]
                ]
            , tr []
                [ td [] [ Html.map FirstSignalMsg (Signal.view model.distantSignal model.signalType) ]
                , td [] [ Html.map FirstSignalMsg (Signal.view model.signalRepeater model.signalType) ]
                , td [] [ Html.map FirstSignalMsg (Signal.view model.combinationSignal model.signalType) ]
                , td [] [ Html.map FirstSignalMsg (Signal.view model.mainSignal model.signalType) ]
                ]
            ]
        ]


distantSignalOptions : (Messages.Msg -> msg) -> SignalModel.Model -> { a | language : Language } -> Html.Html msg
distantSignalOptions targetSignal signal model =
    optionWithoutButton
        { toggleActive = SignalModel.shortenedBrakePath signal
        , toggleMsg = targetSignal ToggleShortBrakePath
        , toggleLabel = translate model "> 5% verkürzter Bremsweg" "> 5% shortened brake path"
        }


mainSignalOptions : (Messages.Msg -> msg) -> SignalModel.Model -> Model -> Html.Html msg
mainSignalOptions targetSignal signal model =
    div []
        [ button [ onClick (targetSignal (SetAspect Stop)) ]
            [ translate model "Halt" "Stop" ]
        , button [ onClick (targetSignal (SetAspect Proceed)) ]
            [ translate model "Fahrt" "Proceed" ]
        , div []
            [ if model.signalType == SignalModel.Hl then
                span []
                    [ label []
                        [ input
                            [ type_ "checkbox"
                            , checked (List.member 6 (SignalModel.slowSpeedLights signal))
                            , onClick (targetSignal (ToggleSlowSpeedLight 6))
                            ]
                            []
                        , translate model "Oranger Streifen (60 km/h)" "Orange stripe (60 km/h)"
                        ]
                    , label []
                        [ input
                            [ type_ "checkbox"
                            , checked (List.member 10 (SignalModel.slowSpeedLights signal))
                            , onClick (targetSignal (ToggleSlowSpeedLight 10))
                            ]
                            []
                        , translate model "Grüner Streifen (100 km/h)" "Green stripe (100 km/h)"
                        ]
                    ]

              else
                span []
                    [ label []
                        [ input
                            [ type_ "radio"
                            , checked (Zs3.isAbsent (SignalModel.zs3 signal))
                            , onClick (targetSignal SetZs3Absent)
                            ]
                            []
                        , translate model "Kein Zs3" "No Zs3"
                        ]
                    , label []
                        [ input
                            [ type_ "radio"
                            , checked (Zs3.isDynamic (SignalModel.zs3 signal))
                            , onClick (targetSignal SetZs3Dynamic)
                            ]
                            []
                        , translate model "Dynamisches Zs3" "Dynamic Zs3"
                        ]
                    , label []
                        [ input
                            [ type_ "radio"
                            , checked (Zs3.isFixed (SignalModel.zs3 signal))
                            , onClick (targetSignal SetZs3Fixed)
                            ]
                            []
                        , translate model "Zs3-Schild" "Zs3 sign"
                        ]
                    , if List.member model.signalType [ SignalModel.HvLight, SignalModel.HvSemaphore ] then
                        label []
                            [ input
                                [ type_ "checkbox"
                                , checked (List.member 4 (SignalModel.slowSpeedLights signal))
                                , onClick (targetSignal (ToggleSlowSpeedLight 4))
                                ]
                                []
                            , translate model "Langsamfahrt" "Proceed slowly"
                            ]

                      else
                        span [] []
                    ]
            , speedDropdown targetSignal (SignalModel.availableSpeedLimits model.signalType signal) (SignalModel.mainSignalSpeedLimit signal)
            ]
        , optionWithButton
            { toggleActive = SignalModel.hasRa12 signal
            , toggleMsg = targetSignal ToggleHasRa12
            , toggleLabel = translate model "Rangierfahrt erlaubt Sh 1/Ra 12" "Shunting permitted Sh 1/Ra 12"
            , actionMsg = targetSignal (SetAspect StopAndRa12)
            , actionLabel = translate model "Aktiv" "Active"
            }
        , optionWithButton
            { toggleActive = SignalModel.hasZs1 signal
            , toggleMsg = targetSignal ToggleHasZs1
            , toggleLabel = translate model "Ersatzsignal Zs1" "Subsidiary signal Zs1"
            , actionMsg = targetSignal (SetAspect StopAndZs1)
            , actionLabel = translate model "Aktiv" "Active"
            }
        , if model.signalType == SignalModel.Hl then
            span [] []

          else
            optionWithButton
                { toggleActive = SignalModel.hasZs7 signal
                , toggleMsg = targetSignal ToggleHasZs7
                , toggleLabel = translate model "Vorsichtsignal Zs7" "Caution signal Zs7"
                , actionMsg = targetSignal (SetAspect StopAndZs7)
                , actionLabel = translate model "Aktiv" "Active"
                }
        ]


translate : { a | language : Language } -> String -> String -> Html.Html msg
translate model german english =
    case model.language of
        German ->
            text german

        English ->
            text english


optionWithoutButton : { b | toggleActive : Bool, toggleLabel : Html.Html a, toggleMsg : a } -> Html.Html a
optionWithoutButton options =
    label []
        [ input
            [ type_ "checkbox"
            , checked options.toggleActive
            , onClick options.toggleMsg
            ]
            []
        , options.toggleLabel
        ]


optionWithButton : { a | actionLabel : Html.Html msg, actionMsg : msg, toggleActive : Bool, toggleLabel : Html.Html msg, toggleMsg : msg } -> Html.Html msg
optionWithButton options =
    div []
        [ optionWithoutButton options
        , button
            [ onClick options.actionMsg
            , disabled (not options.toggleActive)
            ]
            [ options.actionLabel ]
        ]


speedDropdown : (Messages.Msg -> msg) -> List (Maybe Int) -> Maybe Int -> Html.Html msg
speedDropdown targetSignal speedOptions selectedSpeed =
    select
        [ onSelectChange
            (\selectedValue ->
                selectedValue
                    |> String.toInt
                    |> SetSpeedLimit
                    |> targetSignal
            )
        , disabled (List.length speedOptions < 2)
        ]
        (List.map
            (\speedOption ->
                case speedOption of
                    Nothing ->
                        option [ value "infinity", Html.Attributes.selected (selectedSpeed == Nothing) ] [ text "∞" ]

                    Just speed ->
                        option
                            [ value (String.fromInt speed), Html.Attributes.selected (selectedSpeed == Just speed) ]
                            [ text
                                (String.fromInt (speed * 10) ++ " km/h")
                            ]
            )
            speedOptions
        )
