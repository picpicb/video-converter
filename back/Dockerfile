FROM python:3.5-alpine
COPY ca.cert.pem ./
COPY application.yml /home/toto/Téléchargements/video-conversion/back/application.yml
COPY ./requirements.txt ./
RUN apk add --no-cache build-base \
    ffmpeg
RUN pip install -r requirements.txt
ADD . .
CMD ["python","video-conversion-worker.py"]