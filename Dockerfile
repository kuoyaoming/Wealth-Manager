# Use official Android SDK image
FROM openjdk:11-jdk

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

# Install required packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Download and install Android SDK
RUN mkdir -p ${ANDROID_HOME} && \
    cd ${ANDROID_HOME} && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip commandlinetools-linux-9477386_latest.zip && \
    rm commandlinetools-linux-9477386_latest.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools/latest && \
    mv ${ANDROID_HOME}/cmdline-tools/* ${ANDROID_HOME}/cmdline-tools/latest/ 2>/dev/null || true

# Set up Android SDK
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin

# Accept Android SDK licenses and install required packages
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Create local.properties
RUN echo "sdk.dir=${ANDROID_HOME}" > local.properties

# Build the APK
RUN ./gradlew assembleDebug

# Copy APK to output directory
RUN mkdir -p /output && \
    cp app/build/outputs/apk/debug/app-debug.apk /output/WealthManager-v0.1.1-debug.apk