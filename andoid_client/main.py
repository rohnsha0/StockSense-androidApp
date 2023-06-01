import numpy as np
import pandas as pd
from kivy.clock import Clock
from kivymd.app import MDApp
from kivy.uix.boxlayout import BoxLayout
from kivymd.theming import ThemableBehavior
from kivymd.uix.label import MDLabel
from kivymd.uix.spinner import MDSpinner
from kivymd.uix.textfield import MDTextField
from kivymd.uix.button import MDFlatButton
from kivy.metrics import dp
from datetime import datetime, timedelta
from tensorflow import keras
import yfinance as yf
from sklearn.preprocessing import MinMaxScaler


class MyBoxLayout(BoxLayout):
    def __init__(self, **kwargs):
        super(MyBoxLayout, self).__init__(**kwargs)
        self.orientation = "vertical"

        self.input_text = MDTextField(hint_text="Enter your text", size_hint=(0.8, None), height=dp(48), required=True)
        self.input_text.pos_hint = {'center_x': 0.5, 'center_y': 0.5}
        self.add_widget(self.input_text)

        self.submit_button = MDFlatButton(text="Submit", size_hint=(0.4, None), height=dp(48))
        self.submit_button.bind(on_release=self.submit_button_pressed)
        self.submit_button.pos_hint = {'center_x': 0.5, 'center_y': 0.5}
        self.add_widget(self.submit_button)

        self.loading_spinner = MDSpinner(size_hint=(None, None), size=(dp(48), dp(48)), active=False)
        self.add_widget(self.loading_spinner)

        self.prediction_label = MDLabel(
            text="",
            size_hint=(0.8, None),
            height=dp(48),
            halign="center",
            valign="center"
        )
        self.add_widget(self.prediction_label)

    def submit_button_pressed(self, instance):
        user_input = self.input_text.text
        pred=stockPred(user_input)
        self.prediction_label.text = f"Prediction: {pred[0][0]}"


def stockPred(symbol):
    end_date = datetime.now().date()
    start_date = end_date - timedelta(days=45)
    end_date1 = datetime.now().date()
    start_date1 = datetime.now().date() - timedelta(days=14)

    df = yf.download(symbol, interval='30m', start=start_date, end=end_date)
    df = df.reset_index()
    trainSet = df.iloc[:, 1:2].values
    scaler = MinMaxScaler(feature_range=(0, 1))
    trainingSetScaled = scaler.fit_transform(df['Close'].values.reshape(-1, 1))

    xTRAIN = []
    yTRAIN = []
    for i in range(60, len(trainingSetScaled)):
        xTRAIN.append(trainingSetScaled[i - 60:i, 0])
        yTRAIN.append(trainingSetScaled[i, 0])
    xTRAIN, yTRAIN = np.array(xTRAIN), np.array(yTRAIN)

    xTRAIN = np.reshape(xTRAIN, newshape=(xTRAIN.shape[0], xTRAIN.shape[1], 1))
    regressor = keras.models.Sequential([
        keras.layers.LSTM(units=128, return_sequences=True, input_shape=(xTRAIN.shape[1], 1)),
        keras.layers.Dropout(rate=0.2),
        keras.layers.LSTM(units=64, return_sequences=True),
        keras.layers.Dropout(rate=0.3),
        keras.layers.LSTM(units=64),
        keras.layers.Dropout(rate=0.5),
        keras.layers.Dense(units=1)
    ])

    regressor.compile(optimizer='adam', loss=keras.losses.mean_squared_error)
    regressor.fit(x=xTRAIN, y=yTRAIN, batch_size=32, epochs=25)

    # TEST DATA
    testDF = yf.download(symbol, interval='30m', start=start_date1, end=end_date1)
    realSP = testDF['Close'].values
    dfTotal = pd.concat((df['Open'], testDF['Open']), axis=0)
    modelInp = dfTotal[len(dfTotal) - len(testDF) - 60:].values
    modelInp = modelInp.reshape(-1, 1)
    modelInp = scaler.transform(modelInp)

    xTEST = []
    for i in range(60, len(modelInp)):
        xTEST.append(modelInp[i - 60:i, 0])
    xTEST = np.array(xTEST)
    xTEST = np.reshape(xTEST, newshape=(xTEST.shape[0], xTEST.shape[1], 1))
    pred = regressor.predict(xTEST)
    pred = scaler.inverse_transform(pred)

    # making prediction
    realData = [modelInp[len(modelInp) + 1 - 60:len(modelInp + 1), 0]]
    realData = np.array(realData)
    realData = np.reshape(realData, newshape=(realData.shape[0], realData.shape[1], 1))
    prediction = regressor.predict(realData)
    prediction = scaler.inverse_transform(prediction)
    return prediction


class StockSense(MDApp):
    def build(self):
        self.theme_cls.theme_style = "Light"
        self.theme_cls.primary_palette = "BlueGray"
        return MyBoxLayout()


if __name__ == '__main__':
    StockSense().run()
