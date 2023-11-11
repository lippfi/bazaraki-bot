# Bazaraki Alert Bot

## Overview

This Telegram bot was developed to address the challenge of finding apartments in Cyprus, especially during high-demand periods. It was designed to notify users about new apartment listings on Bazaraki, a local website with various ads, tailored to their specific preferences like price, number of rooms, and location.

## Features

-   **Real-time Notifications:** The bot notifies users via Telegram messenger as soon as a property matching their criteria is listed.
-   **Advanced Filtering:** Offers enhanced filtering capabilities compared to Bazaraki's native options, ensuring users receive alerts only for listings that closely match their preferences.
-   **Customizable Search Parameters:** Users can specify their desired apartment characteristics, including price range, number of rooms, and desired locations.

## How It Works

The bot operates by parsing HTML data from Bazaraki since the website does not offer an API for direct integration. This parsing allows the bot to extract information about new advertisements and evaluate them against user-defined criteria.

## Current Status

**Important Update:** As of the latest update, Bazaraki has changed its html code, which has impacted the parsing capabilities of the bot. Currently, the bot is unable to operate as intended due to these changes.
