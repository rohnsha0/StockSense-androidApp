import datetime
import  io
from flask import Flask, render_template, request, Response
import yfinance as yf
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.interval import IntervalTrigger
import utils

app = Flask(__name__)
symbol = "ITC.NS"


def dataRetrieval():
    data = yf.download(tickers=symbol, interval="30m", period="1d")
    with open(file=f'../dataset/{symbol}.csv', mode='a', newline='') as dataUpdated:
        data.to_csv(dataUpdated, header=dataUpdated.tell() == 0)
    print('Data retrieved & Updated at', datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
    return data


scheduler = BackgroundScheduler()
scheduler.add_job(func=dataRetrieval, trigger=IntervalTrigger(minutes=30),
                  start_date=datetime.datetime.now().replace(hour=9, minute=15, second=0),
                  end_date=datetime.datetime.now().replace(hour=15, minute=30, second=0), timezone='Asia/Kolkata')
scheduler.start()


@app.route('/')
def welcome():
    return render_template("app.html")


@app.route('/process_input', methods=['POST'])
def process_input():
    input_text = request.form['input']
    output_text = f"You entered: {input_text}"
    return render_template('app.html', output=output_text)


@app.route('/mom')
def mom():
    utils.loadDS(companyName=symbol)
    return 'data fetched'


if __name__ == '__main__':
    print('Starting Flask Server.....')
    # dataRetrieval()
    app.run()
