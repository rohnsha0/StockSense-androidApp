import datetime
from flask import Flask, render_template, request, Response
import yfinance as yf
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.interval import IntervalTrigger
import utils
import externals

app = Flask(__name__)


@app.route('/')
def welcome():
    return render_template("app.html")


@app.route('/dashboard', methods=['POST'])
def dashboard():
    inputText = request.form['input']
    scriptName, scriptSector = externals.stockName(inputText)
    return render_template("dashboard.html", longName=scriptName, sector=scriptSector)


if __name__ == '__main__':
    print('Starting Flask Server.....')
    # dataRetrieval()
    app.run()
