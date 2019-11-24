FROM python:2.7-alpine
ADD . /source
WORKDIR /source
RUN ls -la
RUN apk add --no-cache build-base \
    ffmpeg
RUN pip install -r requirements.txt
RUN ls -la
ENV GOOGLE_APPLICATION_CREDENTIALS=video-key.json
CMD ["python","-m","video-conversion-worker.py"]

