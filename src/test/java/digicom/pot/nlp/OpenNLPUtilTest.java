/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package digicom.pot.nlp;

import opennlp.tools.util.Span;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;

import digicom.pot.nlp.util.OpenNLPUtil;
import digicom.pot.solr.SearchQueryProcessor;
import digicom.pot.solr.util.BrandHelper;
import digicom.pot.solr.util.ColorHelper;
import digicom.pot.solr.util.PriceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenNLPUtilTest {

	private static OpenNLPUtil extractor;

	static {
		try {
			extractor = new OpenNLPUtil("test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEnglishSentences() {
		String document = "DSLR cameras are fast-focusing, allow you to take multiple photos quickly, and compose sharp images in nearly any light. With a precision viewfinder and image sensors that are more than 8X larger than smartphone sensors, DSLR cameras let you take pictures that are more detailed and stay sharp when resized.";
		for (String sentence : extractor.segmentSentences(document)) {
			System.out.println("sentence: " + sentence);
		}
	}

	// @Test
	public void testEnglishSegmentation() {
		String document = "Don’t write off Microsoft’s chances in mobile just yet. It may still be struggling to make itself count in the smartphone space but early signs are more promising for Windows plus tablets. Microsoft has gone from having no share of the global tablet OS market in Q1 last year to taking 7.4% one year later, with three million Windows 8 tablets shipped in Q1 2013, according to preliminary figures from Strategy Analytics‘ Global Tablet OS Market Share: Q1 2013 report.";

		for (String sentence : extractor.segmentSentences(document)) {
			System.out.println("sentence: " + sentence);

			for (String token : extractor.tokenizeSentence(sentence)) {
				System.out.println("\t" + token);
			}
		}
	}

	@Test
	public void testEnglishNames() {
		String document = "Steve Jobs T-shirt for Men by Levis - New For MEN Size Only";

		for (String sentence : extractor.segmentSentences(document)) {
			System.out.println("sentence: " + sentence);
			String[] tokens = extractor.tokenizeSentence(sentence);
			/*Span[] spans = extractor.findNames(tokens);
			for (Span span : spans) {
				System.out.print("person: ");
				for (int i = span.getStart(); i < span.getEnd(); i++) {
					System.out.print(tokens[i]);
					if (i < span.getEnd()) {
						System.out.print(" ");
					}
				}
				System.out.println();
			}*/
		}
	}

	@Test
	public void testEnglishPartOfSpeech() {
		String document = "The whole package. In a smaller package.iPad mini features a beautiful 7.9-inch display, iSight and FaceTime cameras, the A5 chip, ultrafast wireless, and up to 10 hours of battery life. And over 275,000 apps on the App Store made for iPad also work with iPad mini. So it’s an iPad in every way, shape, and slightly smaller form. It is available in black & slate or white & silver.";
		for (String sentence : extractor.segmentSentences(document)) {
			System.out.println("sentence: " + sentence);
			String[] tokens = extractor.tokenizeSentence(sentence);
			System.out.println("Tokens:" + Arrays.asList(tokens));
			/*
			 * String[] tags = extractor.tagPartOfSpeech(tokens); double[] probs
			 * = extractor.getPartOfSpeechProbabilities();
			 * 
			 * for (int i = 0; i < tokens.length; i++) {
			 * System.out.print("token: " + tokens[i]); System.out.print("\t");
			 * System.out.print("pos: " + tags[i] + " - " + posValue(tags[i]));
			 * System.out.print("\t"); System.out.print("probability: " +
			 * probs[i]); System.out.println(); }
			 */
		}
	}

	@Test
	public void testMoneyFinder() {
		String document = "ipod less than hundred dollars";
		for (String sentence : extractor.segmentSentences(document)) {
			System.out.println("sentence: " + sentence);

			String[] tokens = extractor.tokenizeSentence(sentence);

			for (String token : tokens) {
				System.out.println(token);
			}

			Span[] spans = extractor.findMoney(tokens);

			for (Span span : spans) {

				System.out.print("Money: ");

				for (int i = span.getStart(); i < span.getEnd(); i++) {
					System.out.print(tokens[i]);
					if (i < span.getEnd()) {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
		}
	}

	@Test
	public void testColorFinder() {
		// String document =
		// "red Kitchen Towels.red Kitchen Towels under 15$.silver Platinum Pets � identifies silver as color and Platinum pets as brand.red Progear towel. red Progear towel under 18$.red CHEFS towel under 18$"";
		String document = "red CHEFS towel under 18$";

		ColorHelper colorHelper = new ColorHelper();
		List<String> respn = colorHelper.getColors(document, extractor);
		System.out.println("BH : " + respn);

		for (String sentence : extractor.segmentSentences(document)) {
			// System.out.println("sentence: " + sentence);
			String[] tokens = extractor.tokenizeSentence(sentence);
			// System.out.println(" Tokens " + Arrays.asList(tokens));
			Span[] spans = extractor.findColor(tokens);
			// System.out.println(" Span Size " + spans.length);

			for (Span span : spans) {
				System.out.print("color: ");
				for (int i = span.getStart(); i < span.getEnd(); i++) {
					System.out.print(tokens[i]);
					if (i < span.getEnd()) {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
		}
	}

	@Test
	public void testBrandFinder() {
		String document = "Tops"; // "U Tops"; Brand // U White Tops - Brand +
									// Color
		BrandHelper bhelper = new BrandHelper();
		List<String> respn = bhelper.getBrands(document, extractor);
		System.out.println("BH : " + respn);

		for (String sentence : extractor.segmentSentences(document)) {
			System.out.println("sentence: " + sentence);
			String[] tokens = extractor.tokenizeSentence(sentence);
			Span[] spans = extractor.findBrand(tokens);
			for (Span span : spans) {
				System.out.print("Brand: ");
				for (int i = span.getStart(); i < span.getEnd(); i++) {
					System.out.print(tokens[i]);
					if (i < span.getEnd()) {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
		}
	}

	@Test
	public void testFilters() {
		List<String> queryList = new ArrayList<String>();
		queryList.add("CHEFS knife set");
		queryList.add("CHEFS kitchen towels");
		queryList.add("CHEFS red bath towels");
		queryList.add("bowl set CHEFS 6 pcs");
		queryList.add("U Tops");
		queryList.add("U white Tops");
		queryList.add("U red Tops under 10$");
		queryList.add("U pink Tops less than 10$");
		queryList.add("U pink Tops less than 10  usd");
		queryList.add("red Kitchen Towels");
		queryList.add("red Kitchen Towels under 15$");
		queryList.add("silver Platinum Pets toys");
		queryList.add("red Progear towel");
		queryList.add("red Progear towel under 18$");
		queryList.add("Dickies blue pant");
		queryList.add("Dickies kids clothes");
		queryList.add("black Dickies thermal inner");
		queryList.add("Onyx U sweater");
		queryList.add("Onyx sweater");
		ColorHelper colorHelper = new ColorHelper();
		BrandHelper bhelper = new BrandHelper();
		PriceHelper pricehelper = new PriceHelper();

		SearchQueryProcessor sqp = new SearchQueryProcessor();
		SolrQuery query = new SolrQuery();
		String queryString = null;
		for (String document : queryList) {
			System.out
					.println("=================================================");
			/*
			 * System.out.println("Colors : " + colorHelper.getColors(document,
			 * extractor)); System.out.println("Brand : " +
			 * bhelper.getBrands(document, extractor));
			 * System.out.println("Price : " + pricehelper.parseString(document,
			 * extractor));
			 */
			sqp.applyColorFilter(document, extractor, query);
			sqp.applyBrandFilter(document, extractor, query);
			queryString = sqp.applyPriceFilter(document, extractor, query);
			query.setQuery(queryString);
			System.out.println(query);

		}

	}

	public String posValue(String k) {
		String value = k;
		switch (k) {
		case "CC":
			value = "Coordinating conjunction";
			break;
		case "CD":
			value = "Cardinal number";
			break;
		case "DT":
			value = "Determiner";
			break;
		case "EX":
			value = "Existential there";
			break;
		case "FW":
			value = "Foreign word";
			break;
		case "IN":
			value = "Preposition or subordinating conjunction";
			break;
		case "JJ":
			value = "Adjective";
			break;
		case "JJR":
			value = "Adjective, comparative";
			break;
		case "JJS":
			value = "Adjective, superlative";
			break;
		case "LS":
			value = "List item marker";
			break;
		case "MD":
			value = "Modal";
			break;
		case "NN":
			value = "Noun, singular or mass";
			break;
		case "NNS":
			value = "Noun, plural";
			break;
		case "NNP":
			value = "Proper noun, singular";
			break;
		case "NNPS":
			value = "Proper noun, plural";
			break;
		case "PDT":
			value = "Predeterminer";
			break;
		case "POS":
			value = "Possessive ending";
			break;
		case "PRP":
			value = "Personal pronoun";
			break;
		case "PRP$":
			value = "Possessive pronoun";
			break;
		case "RB":
			value = "Adverb";
			break;
		case "RBR":
			value = "Adverb, comparative";
			break;
		case "RBS":
			value = "Adverb, superlative";
			break;
		case "RP":
			value = "Particle";
			break;
		case "SYM":
			value = "Symbol";
			break;
		case "TO":
			value = "to";
			break;
		case "UH":
			value = "Interjection";
			break;
		case "VB":
			value = "Verb, base form";
			break;
		case "VBD":
			value = "Verb, past tense";
			break;
		case "VBG":
			value = "Verb, gerund or present participle";
			break;
		case "VBN":
			value = "Verb, past participle";
			break;
		case "VBP":
			value = "Verb, non-3rd person singular present";
			break;
		case "VBZ":
			value = "Verb, 3rd person singular present";
			break;
		case "WDT":
			value = "Wh-determiner";
			break;
		case "WP":
			value = "Wh-pronoun";
			break;
		case "WP$":
			value = "Possessive wh-pronoun";
			break;
		case "WRB":
			value = "Wh-adverb";
			break;
		default:
			break;
		}
		return value;
	}

}
