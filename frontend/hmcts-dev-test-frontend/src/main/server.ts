#!/usr/bin/env node
import { app } from './app';

// used by shutdownCheck in readinessChecks
app.locals.shutdown = false;

const port: number = parseInt(process.env.PORT || '3100', 10);

app.listen(port, () => {
  console.log(`Application started: http://localhost:${port}`);
});

function gracefulShutdownHandler(signal: string) {
  console.log(`⚠️ Caught ${signal}, gracefully shutting down. Setting readiness to DOWN`);
  app.locals.shutdown = true;
}

process.on('SIGINT', gracefulShutdownHandler);
process.on('SIGTERM', gracefulShutdownHandler);
