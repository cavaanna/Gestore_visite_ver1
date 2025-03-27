-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 27, 2025 at 06:29 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestione_visite`
--

-- --------------------------------------------------------

--
-- Table structure for table `configuratori`
--

CREATE TABLE `configuratori` (
  `id` int(11) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `configuratori`
--

INSERT INTO `configuratori` (`id`, `nome`, `cognome`, `email`, `password`) VALUES
(1, 'Admin', 'Configuratore', 'admin@example.com', 'admin123'),
(2, 'Super', 'User', 'superuser@example.com', 'super456');

-- --------------------------------------------------------

--
-- Table structure for table `credenziali_temporanee`
--

CREATE TABLE `credenziali_temporanee` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `credenziali_temporanee`
--

INSERT INTO `credenziali_temporanee` (`id`, `username`, `password`) VALUES
(1, 'tempuser1', 'temppass1'),
(2, 'tempuser2', 'temppass2'),
(3, 'tempuser3', 'temppass3');

-- --------------------------------------------------------

--
-- Table structure for table `luoghi`
--

CREATE TABLE `luoghi` (
  `nome` varchar(255) NOT NULL,
  `descrizione` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `luoghi`
--

INSERT INTO `luoghi` (`nome`, `descrizione`) VALUES
('Acquario Marino', 'Un acquario con una grande varietà di specie marine.'),
('Castello Storico', 'Un castello medievale ben conservato.'),
('Museo d\'Arte Moderna', 'Un museo con una vasta collezione di opere d\'arte moderna.'),
('Parco Naturale', 'Un parco con sentieri escursionistici e fauna selvatica.'),
('Teatro Antico', 'Un teatro romano ancora in uso per spettacoli.');

-- --------------------------------------------------------

--
-- Table structure for table `visite`
--

CREATE TABLE `visite` (
  `luogo` varchar(255) DEFAULT NULL,
  `tipo_visita` varchar(255) DEFAULT NULL,
  `volontario` varchar(255) DEFAULT NULL,
  `data` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `visite`
--

INSERT INTO `visite` (`luogo`, `tipo_visita`, `volontario`, `data`) VALUES
('Museo d\'Arte Moderna', 'Arte Moderna', 'Mario Rossi', '2025-03-16'),
('Parco Naturale', 'Escursione', 'Luisa Bianchi', '2025-03-16'),
('Castello Storico', 'Storia Medievale', 'Giulia Verdi', '2025-03-16'),
('Acquario Marino', 'Biologia Marina', 'Alessandro Neri', '2025-03-16'),
('Teatro Antico', 'Spettacolo Teatrale', 'Francesca Gialli', '2025-03-16'),
('Acquario Marino', 'Biologia Marina', 'Mario Rossi', '2025-03-16'),
('Acquario Marino', 'Biologia Marina', 'Mario Rossi', '2025-06-05');

-- --------------------------------------------------------

--
-- Table structure for table `volontari`
--

CREATE TABLE `volontari` (
  `id` int(11) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `tipi_di_visite` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `volontari`
--

INSERT INTO `volontari` (`id`, `nome`, `cognome`, `email`, `password`, `tipi_di_visite`) VALUES
(1, 'Mario', 'Rossi', 'mario.rossi@example.com', 'password123', 'Arte, Storia'),
(2, 'Luisa', 'Bianchi', 'luisa.bianchi@example.com', 'password456', 'Natura, Scienza'),
(3, 'Giulia', 'Verdi', 'giulia.verdi@example.com', 'password789', 'Storia, Natura'),
(4, 'Alessandro', 'Neri', 'alessandro.neri@example.com', 'password321', 'Arte, Scienza'),
(5, 'Francesca', 'Gialli', 'francesca.gialli@example.com', 'password654', 'Natura, Storia');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `configuratori`
--
ALTER TABLE `configuratori`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `credenziali_temporanee`
--
ALTER TABLE `credenziali_temporanee`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `luoghi`
--
ALTER TABLE `luoghi`
  ADD PRIMARY KEY (`nome`);

--
-- Indexes for table `visite`
--
ALTER TABLE `visite`
  ADD KEY `luogo` (`luogo`);

--
-- Indexes for table `volontari`
--
ALTER TABLE `volontari`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `configuratori`
--
ALTER TABLE `configuratori`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `credenziali_temporanee`
--
ALTER TABLE `credenziali_temporanee`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `volontari`
--
ALTER TABLE `volontari`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `visite`
--
ALTER TABLE `visite`
  ADD CONSTRAINT `visite_ibfk_1` FOREIGN KEY (`luogo`) REFERENCES `luoghi` (`nome`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
